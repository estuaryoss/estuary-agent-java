package com.github.estuaryoss.agent.utils;


import com.github.estuaryoss.agent.component.CommandRunner;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.model.api.CommandParallel;
import lombok.SneakyThrows;

import java.time.Duration;
import java.time.LocalDateTime;

public class CommandStatusThread implements Runnable {
    private static final float DENOMINATOR = 1000F;

    private final CommandParallel pCmd;
    private final CommandRunner commandRunner;

    public CommandStatusThread(CommandRunner commandRunner, CommandParallel pCmd) {
        this.commandRunner = commandRunner;
        this.pCmd = pCmd;
    }

    @SneakyThrows
    @Override
    public void run() {
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setDetails(
                commandRunner.getCommandDetails(pCmd.getCommand()));

        pCmd.getCommandsStatus().put(pCmd.getCommand().getCommand(), pCmd.getCommandStatuses().get(pCmd.getThreadId()));
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setFinishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setDuration(Duration.between(
                LocalDateTime.parse(pCmd.getCommandStatuses().get(pCmd.getThreadId()).getStartedat(), DateTimeConstants.PATTERN),
                LocalDateTime.parse(pCmd.getCommandStatuses().get(pCmd.getThreadId()).getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
        pCmd.getCommandStatuses().get(pCmd.getThreadId()).setStatus("finished");
        pCmd.getCommandDescription().setCommands(pCmd.getCommandsStatus());
        pCmd.getCommandDescription().setFinishedat(LocalDateTime.now().format(DateTimeConstants.PATTERN));
        pCmd.getCommandDescription().setDuration(Duration.between(
                LocalDateTime.parse(pCmd.getCommandDescription().getStartedat(), DateTimeConstants.PATTERN),
                LocalDateTime.parse(pCmd.getCommandDescription().getFinishedat(), DateTimeConstants.PATTERN)).toMillis() / DENOMINATOR);
        pCmd.getCommandDescription().setFinished(true);
        pCmd.getCommandDescription().setStarted(false);
    }
}
