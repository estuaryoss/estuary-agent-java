package com.github.estuaryoss.agent.component;

import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.constants.DefaultConstants;
import com.github.estuaryoss.agent.constants.EnvConstants;
import com.github.estuaryoss.agent.entity.Command;
import com.github.estuaryoss.agent.model.ExecutionStatus;
import com.github.estuaryoss.agent.model.ProcessState;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.model.api.CommandDetails;
import com.github.estuaryoss.agent.model.api.CommandParallel;
import com.github.estuaryoss.agent.model.api.CommandStatus;
import com.github.estuaryoss.agent.service.DbService;
import com.github.estuaryoss.agent.utils.CommandStatusThread;
import com.github.estuaryoss.agent.utils.ProcessUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.*;
import static com.github.estuaryoss.agent.utils.StringUtils.trimString;

@Component
@Slf4j
public class CommandRunner {
    private static final String EXEC_WIN = "cmd.exe";
    private static final String ARGS_WIN = "/c";
    private static final String EXEC_LINUX = "/bin/sh";
    private static final String ARGS_LINUX = "-c";
    public static final float DENOMINATOR = 1000F;
    public static final String ARGS_DELIMITER = ",";

    private final DbService dbService;
    private final AppEnvironment environment;
    private final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSSSS";

    @Autowired
    public CommandRunner(@Nullable DbService dbService, AppEnvironment environment) {
        this.dbService = dbService;
        this.environment = environment;
    }

    /**
     * Runs a single system command
     *
     * @param command The command to be executed
     * @return The details of the command
     * @throws IOException if the process could not be started
     */
    public CommandDetails runCommand(Command command) throws IOException {
        return this.getCommandDetails(command);
    }

    /**
     * Runs the system commands sequentially, one after the other
     *
     * @param commands The system commands to be executed
     * @return The description of all commands
     * @throws IOException if the process could not be started
     */
    public CommandDescription runCommands(String[] commands) throws IOException {
        LinkedHashMap commandsStatus = new LinkedHashMap<String, CommandStatus>();
        CommandDescription commandDescription = CommandDescription.builder()
                .startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .started(true)
                .finished(false)
                .pid(ProcessHandle.current().pid())
                .build();

        List<Command> commandsDbList = new ArrayList<>();
        for (String cmd : commands) {
            Command command = Command.builder()
                    .command(trimString(cmd, COMMAND_MAX_SIZE))
                    .status(ExecutionStatus.QUEUED.getStatus())
                    .build();
            if (dbService != null) dbService.saveCommand(command);

            commandsDbList.add(command);
        }
        for (Command cmd : commandsDbList) {
            CommandStatus commandStatus = new CommandStatus();
            commandStatus.setStartedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            CommandDetails commandDetails = this.runCommand(cmd);
            commandStatus.setDetails(commandDetails);
            commandStatus.setFinishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandStatus.setDuration(Duration.between(
                    LocalDateTime.parse(commandStatus.getStartedat(), DateTimeConstants.PATTERN),
                    LocalDateTime.parse(commandStatus.getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
            commandStatus.setStatus(ExecutionStatus.FINISHED.getStatus());
            commandsStatus.put(cmd.getCommand(), commandStatus);
            commandDescription.setCommands(commandsStatus);
        }

        commandDescription.setFinishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
        commandDescription.setDuration(Duration.between(
                LocalDateTime.parse(commandDescription.getStartedat(), DateTimeConstants.PATTERN),
                LocalDateTime.parse(commandDescription.getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
        commandDescription.setFinished(true);
        commandDescription.setStarted(false);


        return commandDescription;
    }

    /**
     * Runs one command in background, aka Non-blocking mode.
     *
     * @param command The system command to be executed
     * @return A reference to a {@link ProcessExecutor}
     * @throws IOException if the process could not be started
     */
    public ProcessState runCommandDetached(String[] command) throws IOException {
        return this.runCmdDetached(command);
    }

    /**
     * Runs the system commands in parallel using multi-processes
     *
     * @param commands The system commands to be executed in parallel
     * @return The description of all commands
     * @throws IOException if the process could not be started
     */
    public CommandDescription runCommandsParallel(String[] commands) throws IOException {
        ArrayList<ProcessState> processStates = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<CommandStatus> commandStatuses = new ArrayList<>();
        LinkedHashMap<String, CommandStatus> commandsStatus = new LinkedHashMap();
        CommandDescription commandDescription = CommandDescription.builder()
                .startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .started(true)
                .finished(false)
                .pid(ProcessHandle.current().pid())
                .build();
        for (int i = 0; i < commands.length; i++) {
            commandStatuses.add(new CommandStatus());
            commandStatuses.get(i).setStartedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            ProcessState processState = this.runCommandDetached(commands[i].split(" "));
            processStates.add(processState);
        }

        //start threads that reads the stdout, stderr, pid and others
        for (int i = 0; i < processStates.size(); i++) {
            Command command = Command.builder()
                    .command(commands[i])
                    .status(ExecutionStatus.QUEUED.getStatus())
                    .build();
            if (dbService != null) dbService.saveCommand(command);
            CommandStatusThread cmdStatusThread = new CommandStatusThread(this, CommandParallel.builder()
                    .commandDescription(commandDescription)
                    .commandStatuses(commandStatuses)
                    .commandsStatus(commandsStatus)
                    .command(command)
                    .processState(processStates.get(i))
                    .threadId(i)
                    .build());
            threads.add(new Thread(cmdStatusThread));
            threads.get(i).start();
        }

        //join threads
        for (int i = 0; i < processStates.size(); i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                log.debug(ExceptionUtils.getStackTrace(e));
            }
        }

        return commandDescription;
    }

    /**
     * @param processState A reference to a {@link ProcessState}
     * @param commandDb    A Command object representation from the DB
     * @return The command details of the command executed
     */
    @SneakyThrows
    private CommandDetails getCommandDetailsFromProcess(ProcessState processState, Command commandDb) {
        CommandDetails commandDetails;
        InputStream inputStream = null;
        int timeout = environment.getEnv().get(EnvConstants.COMMAND_TIMEOUT) != null ?
                Integer.parseInt(environment.getEnv().get(EnvConstants.COMMAND_TIMEOUT)) : DefaultConstants.COMMAND_TIMEOUT_DEFAULT;

        try {
            ProcessResult processResult = processState.getProcessResult().get(timeout, TimeUnit.SECONDS);

            int code = processResult.getExitValue();
            String out = processResult.getOutput().getString();
            inputStream = new ByteArrayInputStream(processState.getErrOutputStream().toByteArray());
            String err = IOUtils.toString(inputStream, Charset.defaultCharset());

            commandDetails = CommandDetails.builder()
                    .out(out)
                    .err(err)
                    .code(code)
                    .pid(processState.getProcess().pid())
                    .args(commandDb.getArgs().split(ARGS_DELIMITER))
                    .build();

            commandDb.setOut(trimString(out, COMMAND_STDOUT_MAX_SIZE));
            commandDb.setErr(trimString(err, COMMAND_STDERR_MAX_SIZE));
            commandDb.setCode(Long.valueOf(code));
            commandDb.setFinishedAt(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandDb.setStatus(ExecutionStatus.FINISHED.getStatus());
            commandDb.setDuration(Duration.between(
                    LocalDateTime.parse(commandDb.getStartedAt(), DateTimeConstants.PATTERN),
                    LocalDateTime.parse(commandDb.getFinishedAt(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
        } catch (TimeoutException e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            commandDetails = CommandDetails.builder()
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(DefaultConstants.PROCESS_EXCEPTION_TIMEOUT)
                    .args(commandDb.getArgs().split(ARGS_DELIMITER))
                    .build();

            commandDb.setOut("");
            commandDb.setErr(trimString(ExceptionUtils.getStackTrace(e), COMMAND_STDERR_MAX_SIZE));
            commandDb.setCode(Long.valueOf(DefaultConstants.PROCESS_EXCEPTION_TIMEOUT));
            commandDb.setFinishedAt(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandDb.setStatus(ExecutionStatus.FINISHED.getStatus());
            commandDb.setDuration(Duration.between(
                    LocalDateTime.parse(commandDb.getStartedAt(), DateTimeConstants.PATTERN),
                    LocalDateTime.parse(commandDb.getFinishedAt(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);

            ProcessUtils.killProcessAndChildren(processState.getProcess().pid());
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            commandDetails = CommandDetails.builder()
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(DefaultConstants.PROCESS_EXCEPTION_GENERAL)
                    .args(commandDb.getArgs().split(ARGS_DELIMITER))
                    .build();

            commandDb.setOut("");
            commandDb.setErr(trimString(ExceptionUtils.getStackTrace(e), COMMAND_STDERR_MAX_SIZE));
            commandDb.setCode(Long.valueOf(DefaultConstants.PROCESS_EXCEPTION_GENERAL));
            commandDb.setFinishedAt(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandDb.setStatus(ExecutionStatus.FINISHED.getStatus());
            commandDb.setDuration(Duration.between(
                    LocalDateTime.parse(commandDb.getStartedAt(), DateTimeConstants.PATTERN),
                    LocalDateTime.parse(commandDb.getFinishedAt(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
        } finally {
            try {
                processState.closeErrOutputStream();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                log.debug(ExceptionUtils.getStackTrace(e));
            }
        }

        if (dbService != null) dbService.saveAndFlushCommand(commandDb);

        return commandDetails;
    }

    private ArrayList<String> getPlatformCommand() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> platformCmd = new ArrayList<>();

        if (isWindows) {
            platformCmd.add(EXEC_WIN);
            platformCmd.add(ARGS_WIN);
        } else {
            platformCmd.add(EXEC_LINUX);
            platformCmd.add(ARGS_LINUX);
        }

        return platformCmd;
    }

    private ProcessState runCmdDetached(String[] cmd) throws IOException {
        ArrayList<String> fullCommand = getPlatformCommand();
        fullCommand.add(String.join(" ", cmd));

        ProcessState processState = getProcessState(fullCommand.toArray(new String[0]));

        return processState;
    }

    public CommandDetails getCommandDetails(Command cmd) throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        List<String> fullCommand = getPlatformCommand();
        String commandWithSingleSpaces = cmd.getCommand().trim().replaceAll("\\s+", " ");

        if (isWindows) {
            for (String cmdPart : commandWithSingleSpaces.split(" ")) {
                fullCommand.add(cmdPart);
            }
        } else {
            fullCommand.add(cmd.getCommand());
        }

        ProcessState processState = getProcessState(fullCommand.toArray(new String[0]));
        cmd.setCommand(trimString(commandWithSingleSpaces, COMMAND_MAX_SIZE));
        cmd.setArgs(trimString(String.join(ARGS_DELIMITER, fullCommand), COMMAND_MAX_SIZE));
        cmd.setStartedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
        cmd.setPid(processState.getProcess().pid());
        cmd.setStatus(ExecutionStatus.RUNNING.getStatus());

        if (dbService != null) dbService.saveCommand(cmd);

        return this.getCommandDetailsFromProcess(processState, cmd);
    }

    private ProcessState getProcessState(String[] command) throws IOException {
        ProcessState processState = new ProcessState();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StartedProcess startedProcess = new ProcessExecutor()
                .command(command)
                .environment(environment.getEnvAndVirtualEnv())
                .destroyOnExit()
                .readOutput(true)
                .redirectError(outputStream)
                .start();


        processState.startedProcess(startedProcess);
        processState.process(startedProcess.getProcess());
        processState.processResult(startedProcess.getFuture());
        processState.errOutputStream(outputStream);

        return processState;
    }

    private String doQuoteCmd(String s) {
        return "\"" + s + "\"";
    }
}
