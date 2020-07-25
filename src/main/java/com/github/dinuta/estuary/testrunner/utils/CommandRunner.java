package com.github.dinuta.estuary.testrunner.utils;

import com.github.dinuta.estuary.testrunner.constants.DateTimeConstants;
import com.github.dinuta.estuary.testrunner.model.api.CommandDescription;
import com.github.dinuta.estuary.testrunner.model.api.CommandDetails;
import com.github.dinuta.estuary.testrunner.model.api.CommandParallel;
import com.github.dinuta.estuary.testrunner.model.api.CommandStatus;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class CommandRunner {
    private static final float DENOMINATOR = 1000F;

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
     * @return A reference to an Optional of Process
     */
    public Optional<Process> runStartCommandDetached(List<String> command) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = getPlatformCommand();

        if (isWindows) {
            fullCommand.add(String.format("%s/start.py", Paths.get("").toAbsolutePath().toString()));
            for (String cmd : command) {
                fullCommand.add(this.doQuoteCmd(cmd));
            }
        } else {
            fullCommand.add(String.format("%s/start.py ", Paths.get("").toAbsolutePath().toString())
                    + this.doQuoteCmd(command.get(0)) + " " + this.doQuoteCmd(command.get(1)));
        }

        fullCommand.add(this.doQuoteCmd(String.join(" ", command)));

        return this.runCmdDetached(fullCommand.toArray(new String[0]));
    }

    /**
     * Runs one command in detached mode, aka Non-blocking mode.
     *
     * @param command The system command to be executed
     * @return A reference to an Optional of Process
     */
    public Optional<Process> runCommandDetached(String[] command) {
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
        ArrayList<Optional> processes = new ArrayList<>();
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
            processes.add(this.runCommandDetached(commands[i].split(" ")));
        }

        //start threads that reads the stdout, stderr, pid and others
        for (int i = 0; i < processes.size(); i++) {
            threads.add(new Thread(
                    new CommandStatusThread(new CommandParallel()
                            .commandDescription(commandDescription)
                            .commandStatuses(commandStatuses)
                            .commandsStatus(commandsStatus)
                            .command(commands[i])
                            .process(processes.get(i))
                            .threadId(i)
                    )));
            threads.get(i).start();
        }

        //join threads
        for (int i = 0; i < processes.size(); i++) {
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
     * @param command           The command to be executed
     * @param optionalOfProcess An optional reference to a previously started process
     * @return The command details of the previously executed command
     */
    public CommandDetails getCmdDetailsOfProcess(String[] command, Optional optionalOfProcess) {
        CommandDetails commandDetails = new CommandDetails();
        String out = "";
        String err = "";
        String s;
        Process process = (Process) optionalOfProcess.get();

        try {
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                out += s + "\n";
            }

            while ((s = stdError.readLine()) != null) {
                err += s + "\n";
            }

            commandDetails
                    .out(out.stripLeading().stripTrailing())
                    .err(err.stripLeading().stripTrailing())
                    .code(process.waitFor())
                    .pid(process.pid())
                    .args(command);

        } catch (Exception e) {
            e.printStackTrace();
            commandDetails
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(1)
                    .args(command);
        }

        return commandDetails;
    }

    private ArrayList<String> getPlatformCommand() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = new ArrayList<>();

        if (isWindows) {
            fullCommand.add("cmd.exe");
            fullCommand.add("/c");
        } else {
            fullCommand.add("/bin/sh");
            fullCommand.add("-c");

        }

        return fullCommand;
    }

    private Optional<Process> runCmdDetached(String[] command) {
        Process process = getProcess(command);

        return Optional.of(process);
    }

    private CommandDetails getCommandDetails(String[] command) {
        Process process = getProcess(command);

        return this.getCmdDetailsOfProcess(command, Optional.of(process));
    }

    private Process getProcess(String[] command) {
        Process process = null;
        try {
            process = new ProcessBuilder().command(command).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    private String doQuoteCmd(String s) {
        return "\"" + s + "\"";
    }
}
