package com.github.dinuta.estuary.testrunner.utils;

import com.github.dinuta.estuary.testrunner.constants.DateTimeConstants;
import com.github.dinuta.estuary.testrunner.model.api.CommandDescription;
import com.github.dinuta.estuary.testrunner.model.api.CommandDetails;
import com.github.dinuta.estuary.testrunner.model.api.CommandParallel;
import com.github.dinuta.estuary.testrunner.model.api.CommandStatus;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

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

import static com.github.dinuta.estuary.testrunner.constants.DefaultConstants.*;
import static com.github.dinuta.estuary.testrunner.constants.EnvConstants.COMMAND_TIMEOUT;

public class CommandRunner {
    private static final float DENOMINATOR = 1000F;
    private static final String EXEC_WIN = "cmd.exe";
    private static final String ARGS_WIN = "/c";
    private static final String EXEC_LINUX = "/bin/sh";
    private static final String ARGS_LINUX = "-c";

    /**
     * Runs a single system command
     *
     * @param command The command to be executed
     * @return The details of the command
     */
    public CommandDetails runCommand(String command) {
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
     */
    public CommandDescription runCommands(String[] commands) {
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
     * Ref: https://github.com/dinuta/estuary-testrunner/releases.
     * This start.py is platform dependent and it must be downloaded in the same path along with this jar
     *
     * @param command The commands to be executed separated by semicolon ;
     * @return A reference to a Future of ProcessResult
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
     * @return A reference to a ProcessExecutor
     */
    public ProcessExecutor runCommandDetached(String[] command) {
        ArrayList<String> fullCommand = getPlatformCommand();
        fullCommand.add(String.join(" ", command));

        return this.runCmdDetached(fullCommand.toArray(new String[0]));
    }

    /**
     * Runs the system commands in parallel using multi-processes
     *
     * @param commands The system commands to be executed in parallel
     * @return The description of all commands
     */
    public CommandDescription runCommandsParallel(String[] commands) {
        ArrayList<ProcessExecutor> processExecutors = new ArrayList<>();
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
            processExecutors.add(this.runCommandDetached(commands[i].split(" ")));
        }

        //start threads that reads the stdout, stderr, pid and others
        for (int i = 0; i < processExecutors.size(); i++) {
            threads.add(new Thread(
                    new CommandStatusThread(new CommandParallel()
                            .commandDescription(commandDescription)
                            .commandStatuses(commandStatuses)
                            .commandsStatus(commandsStatus)
                            .command(commands[i])
                            .process(processExecutors.get(i))
                            .threadId(i)
                    )));
            threads.get(i).start();
        }

        //join threads
        for (int i = 0; i < processExecutors.size(); i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return commandDescription;
    }

    /**
     * Used in conjunction with {@link #runCmdDetached(String[] command)}
     * The process was already started and a process reference is passed to it
     *
     * @param command         The command to be executed
     * @param processExecutor An optional reference to a previously started process
     * @return The command details of the previously executed command
     */
    public CommandDetails getCmdDetailsOfProcess(String[] command, ProcessExecutor processExecutor) {
        CommandDetails commandDetails = new CommandDetails();

        try {
            ProcessResult processResult = processExecutor.execute();
            int code = processResult.getExitValue();
            String out = (code == 0) ? processResult.getOutput().getString() : "";
            String err = (code == 0) ? "" : processResult.getOutput().getString();

            commandDetails
                    .out(out)
                    .err(err)
                    .code(code)
                    .pid(PROCESS_ID_DEFAULT)
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

    private ProcessExecutor runCmdDetached(String[] command) {
        return getProcessExecutor(command);
    }

    private CommandDetails getCommandDetails(String[] command) {
        ProcessExecutor pExecutor = getProcessExecutor(command);

        return this.getCmdDetailsOfProcess(command, pExecutor);
    }

    private ProcessExecutor getProcessExecutor(String[] command) {
        int timeout = System.getenv(COMMAND_TIMEOUT) != null ?
                Integer.parseInt(System.getenv(COMMAND_TIMEOUT)) : COMMAND_TIMEOUT_DEFAULT;

        return new ProcessExecutor()
                .command(command)
                .timeout(timeout, TimeUnit.SECONDS)
                .destroyOnExit()
                .readOutput(true);
    }

    private String doQuoteCmd(String s) {
        return "\"" + s + "\"";
    }
}
