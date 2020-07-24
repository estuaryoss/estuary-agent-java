package com.github.dinuta.estuary.testrunner.utils;

import com.github.dinuta.estuary.testrunner.model.api.CommandParallel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandStatusThread implements Runnable {
    private static final float DENOMINATOR = 1000F;
    private CommandRunner commandRunner = new CommandRunner();
    private CommandParallel pCmd;

    public CommandStatusThread(CommandParallel commandParallel) {
        this.pCmd = commandParallel;
    }

    @Override
    public void run() {
        pCmd.getCmdsStatus().put(pCmd.getCmd(), pCmd.getCmdStatuses().get(pCmd.getId()).details(
                commandRunner.getCmdDetailsOfProcess(new String[]{pCmd.getCmd()}, pCmd.getProcess())));
        LocalDateTime end = LocalDateTime.now();
        pCmd.getCmdStatuses().get(pCmd.getId()).finishedat(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        pCmd.getCmdStatuses().get(pCmd.getId()).duration(Duration.between(
                LocalDateTime.parse(pCmd.getCmdStatuses().get(pCmd.getId()).getStartedat(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")),
                end).toMillis() / DENOMINATOR);
        pCmd.getCmdStatuses().get(pCmd.getId()).status("finished");
        pCmd.getCmdDescription().commands(pCmd.getCmdsStatus());
        LocalDateTime endTotal = LocalDateTime.now();
        pCmd.getCmdDescription().finishedat(endTotal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        pCmd.getCmdDescription().duration(Duration.between(
                LocalDateTime.parse(pCmd.getCmdDescription().getStartedat(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")),
                endTotal).toMillis() / DENOMINATOR);
        pCmd.getCmdDescription().finished(true);
        pCmd.getCmdDescription().started(false);
    }
}
