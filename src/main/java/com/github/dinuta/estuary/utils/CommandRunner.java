package com.github.dinuta.estuary.utils;

import com.github.dinuta.estuary.model.CommandDescription;
import com.github.dinuta.estuary.model.CommandDetails;
import com.github.dinuta.estuary.model.CommandStatus;
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

public class CommandRunner {

    public CommandDetails runCommand(String command) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = new ArrayList<>();
        String commandWithSingleSpaces = command.trim().replaceAll("\\s+", " ");

        if (isWindows) {
            fullCommand.add("cmd.exe");
            fullCommand.add("/c");
            for (String cmd : commandWithSingleSpaces.split(" ")) {
                fullCommand.add(cmd);
            }
        } else {
            fullCommand.add("/bin/sh");
            fullCommand.add("-c");
            fullCommand.add(command);
        }

        return this.runCmd(fullCommand.toArray(new String[0]));
    }

    public void runCommandDetached(List<String> command) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ArrayList<String> fullCommand = new ArrayList<>();

        if (isWindows) {
            fullCommand.add("cmd.exe");
            fullCommand.add("/c");
            fullCommand.add(String.format("%s/start.py", Paths.get("").toAbsolutePath().toString()));
            for (String cmd : command) {
                fullCommand.add(this.doQuoteString(cmd));
            }
        } else {
            fullCommand.add("/bin/sh");
            fullCommand.add("-c");
            fullCommand.add(String.format("%s/start.py ", Paths.get("").toAbsolutePath().toString())
                    + this.doQuoteString(command.get(0)) + " " + this.doQuoteString(command.get(1)));
        }

        this.runCmdDetached(fullCommand.toArray(new String[0]));
    }

    public CommandDescription runCommands(String[] commands) {
        CommandDescription commandDescription = new CommandDescription();
        LinkedHashMap command = new LinkedHashMap<String, CommandStatus>();

        commandDescription.startedat(LocalDateTime.now());
        commandDescription.started(true);
        commandDescription.finished(false);
        commandDescription.pid(ProcessHandle.current().pid());

        for (String cmd : commands) {
            CommandStatus commandStatus = new CommandStatus();
            commandStatus.startedat(LocalDateTime.now());
            command.put(cmd, commandStatus.details(this.runCommand(cmd)));
            commandStatus.finishedat(java.time.LocalDateTime.now());
            commandStatus.duration(
                    Duration.between(commandStatus.getStartedat(), commandStatus.getFinishedat()).toSeconds());
            commandStatus.status("finished");
            commandDescription.commands(command);
        }

        commandDescription.finishedat(LocalDateTime.now());
        commandDescription.duration(
                Duration.between(commandDescription.getStartedat(), commandDescription.getFinishedat()).toSeconds());
        commandDescription.finished(true);
        commandDescription.started(false);

        return commandDescription;
    }

    private void runCmdDetached(String[] fullCommand) {
        try {
            Runtime.getRuntime().exec(fullCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CommandDetails runCmd(String[] fullCommand) {
        CommandDetails commandDetails = new CommandDetails();
        String out = "";
        String err = "";
        String s;
        try {
            Process process = Runtime.getRuntime().exec(fullCommand);

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
                    .args(fullCommand);

        } catch (Exception e) {
            e.printStackTrace();
            commandDetails
                    .err(ExceptionUtils.getStackTrace(e))
                    .code(1)
                    .args(fullCommand);
        }

        return commandDetails;
    }

    private String doQuoteString(String s) {
        return "\"" + s + "\"";
    }
}
