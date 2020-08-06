package com.github.dinuta.estuary.agent.utils;

import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.model.ProcessState;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import com.github.dinuta.estuary.agent.model.api.CommandDetails;
import com.github.dinuta.estuary.agent.model.api.CommandParallel;
import com.github.dinuta.estuary.agent.model.api.CommandStatus;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.dinuta.estuary.agent.constants.DefaultConstants.*;
import static com.github.dinuta.estuary.agent.constants.EnvConstants.COMMAND_TIMEOUT;

public class CommandRunner {
    private static final String EXEC_WIN = "cmd.exe";
    private static final String ARGS_WIN = "/c";
    private static final String EXEC_LINUX = "/bin/sh";
    private static final String ARGS_LINUX = "-c";

    private static final float DENOMINATOR = 1000F;

    /**
     * Runs a single system command
     *
     * @param command The command to be executed
     * @return The details of the command
     * @throws IOException if the process could not be started
     */
    public CommandDetails runCommand(String command) throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = getPlatformCommand();
        String commandWithSingleSpaces = command.trim().replaceAll("\\s+", " ");

        if (isWindows) {
            for (String cmd : commandWithSingleSpaces.split(" ")) {
                fullCommand.add(cmd);
            }
        } else {
            fullCommand.add(command);
        }

        return this.getCommandDetails(fullCommand.toArray(new String[0]));
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
        CommandDescription commandDescription = new CommandDescription();

        commandDescription.startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
        commandDescription.started(true);
        commandDescription.finished(false);
        commandDescription.pid(ProcessHandle.current().pid());

        for (String cmd : commands) {
            CommandStatus commandStatus = new CommandStatus();
            commandStatus.startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandsStatus.put(cmd, commandStatus.details(this.runCommand(cmd)));
            commandStatus.finishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            commandStatus.duration(Duration.between(
                    LocalDateTime.parse(commandStatus.getStartedat(), DateTimeConstants.PATTERN),
                    LocalDateTime.parse(commandStatus.getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
            commandStatus.status("finished");
            commandDescription.commands(commandsStatus);
        }

        commandDescription.finishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
        commandDescription.duration(Duration.between(
                LocalDateTime.parse(commandDescription.getStartedat(), DateTimeConstants.PATTERN),
                LocalDateTime.parse(commandDescription.getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
        commandDescription.finished(true);
        commandDescription.started(false);

        return commandDescription;
    }

    /**
     * Runs the commands through the start.py script of the original python implementation.
     * Ref: https://github.com/dinuta/estuary-agent/releases.
     * This start.py is platform dependent and it must be downloaded in the same path along with this jar
     *
     * @param command The commands to be executed separated by semicolon ;
     * @return A reference to a Future of {@link ProcessResult}
     * @throws IOException if the process could not be started
     */
    public Future<ProcessResult> runStartCommandDetached(List<String> command) throws IOException {
        String pythonExec = "start.py";
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = getPlatformCommand();

        if (isWindows) {
            fullCommand.add(String.format("%s/%s ", Paths.get("").toAbsolutePath().toString(), pythonExec));
            for (String cmd : command) {
                fullCommand.add(this.doQuoteCmd(cmd));
            }
        } else {
            fullCommand.add(String.format("%s/%s ", Paths.get("").toAbsolutePath().toString(), pythonExec)
                    + this.doQuoteCmd(command.get(0)) + " " + this.doQuoteCmd(command.get(1)));
        }

        fullCommand.add(this.doQuoteCmd(String.join(" ", command)));

        return this.runStartCmdDetached(fullCommand.toArray(new String[0])).start().getFuture();
    }

    /**
     * Runs one command in detached mode, aka Non-blocking mode.
     *
     * @param command The system command to be executed
     * @return A reference to a {@link ProcessExecutor}
     * @throws IOException if the process could not be started
     */
    public ProcessState runCommandDetached(String[] command) throws IOException {
        ArrayList<String> fullCommand = getPlatformCommand();
        fullCommand.add(String.join(" ", command));

        return this.runCmdDetached(fullCommand.toArray(new String[0]));
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
        CommandDescription commandDescription = new CommandDescription();

        commandDescription.startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
        commandDescription.started(true);
        commandDescription.finished(false);
        commandDescription.pid(ProcessHandle.current().pid());

        for (int i = 0; i < commands.length; i++) {
            commandStatuses.add(new CommandStatus());
            commandStatuses.get(i).startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
            processStates.add(this.runCommandDetached(commands[i].split(" ")));
        }

        //start threads that reads the stdout, stderr, pid and others
        for (int i = 0; i < processStates.size(); i++) {
            threads.add(new Thread(
                    new CommandStatusThread(new CommandParallel()
                            .commandDescription(commandDescription)
                            .commandStatuses(commandStatuses)
                            .commandsStatus(commandsStatus)
                            .command(commands[i])
                            .process(processStates.get(i))
                            .threadId(i)
                    )));
            threads.get(i).start();
        }

        //join threads
        for (int i = 0; i < processStates.size(); i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return commandDescription;
    }

    /**
     * @param command      The command to be executed
     * @param processState A reference to a {@link ProcessState}
     * @return The command details of the command executed
     */
    public CommandDetails getCmdDetailsOfProcess(String[] command, ProcessState processState) {
        CommandDetails commandDetails = new CommandDetails();
        int timeout = System.getenv(COMMAND_TIMEOUT) != null ?
                Integer.parseInt(System.getenv(COMMAND_TIMEOUT)) : COMMAND_TIMEOUT_DEFAULT;

        try {
            ProcessResult processResult = processState.getProcessResult().get(timeout, TimeUnit.SECONDS);
            int code = processResult.getExitValue();
            String out = (code == 0) ? processResult.getOutput().getString() : "";
            String err = (code == 0) ? "" : processResult.getOutput().getString();

            commandDetails
                    .out(out)
                    .err(err)
                    .code(code)
                    .pid(processState.getProcess().pid())
                    .args(command);
        } catch (TimeoutException e) {
            e.printStackTrace();
            commandDetails
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(PROCESS_EXCEPTION_TIMEOUT)
                    .args(command);
        } catch (Exception e) {
            e.printStackTrace();
            commandDetails
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(PROCESS_EXCEPTION_GENERAL)
                    .args(command);
        }

        return commandDetails;
    }

    private ArrayList<String> getPlatformCommand() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = new ArrayList<>();

        if (isWindows) {
            fullCommand.add(EXEC_WIN);
            fullCommand.add(ARGS_WIN);
        } else {
            fullCommand.add(EXEC_LINUX);
            fullCommand.add(ARGS_LINUX);

        }

        return fullCommand;
    }

    private ProcessExecutor runStartCmdDetached(String[] command) {
        return new ProcessExecutor()
                .command(command)
                .destroyOnExit()
                .readOutput(true);
    }

    private ProcessState runCmdDetached(String[] command) throws IOException {
        return getProcessState(command);
    }

    private CommandDetails getCommandDetails(String[] command) throws IOException {
        ProcessState pState = getProcessState(command);

        return this.getCmdDetailsOfProcess(command, pState);
    }

    private ProcessState getProcessState(String[] command) throws IOException {
        ProcessState processState = new ProcessState();
        StartedProcess startedProcess = new ProcessExecutor()
                .command(command)
                .destroyOnExit()
                .readOutput(true).start();

        processState.startedProcess(startedProcess);
        processState.process(startedProcess.getProcess());
        processState.processResult(startedProcess.getFuture());

        return processState;
    }

    private String doQuoteCmd(String s) {
        return "\"" + s + "\"";
    }
}