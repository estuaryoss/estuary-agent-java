package com.github.dinuta.estuary.testrunner.utils;

import com.github.dinuta.estuary.testrunner.model.CommandParallel;

import java.time.Duration;
import java.time.LocalDateTime;

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
        pCmd.getCmdStatuses().get(pCmd.getId()).finishedat(LocalDateTime.now());
        pCmd.getCmdStatuses().get(pCmd.getId()).duration(Duration.between(
                pCmd.getCmdStatuses().get(pCmd.getId()).getStartedat(),
                pCmd.getCmdStatuses().get(pCmd.getId()).getFinishedat()).toMillis() / DENOMINATOR);
        pCmd.getCmdStatuses().get(pCmd.getId()).status("finished");
        pCmd.getCmdDescription().commands(pCmd.getCmdsStatus());
        pCmd.getCmdDescription().finishedat(LocalDateTime.now());
        pCmd.getCmdDescription().duration(Duration.between(
                pCmd.getCmdDescription().getStartedat(),
                pCmd.getCmdDescription().getFinishedat()).toMillis() / DENOMINATOR);
        pCmd.getCmdDescription().finished(true);
        pCmd.getCmdDescription().started(false);
    }
}
