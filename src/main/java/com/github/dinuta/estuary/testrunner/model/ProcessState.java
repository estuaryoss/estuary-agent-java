package com.github.dinuta.estuary.testrunner.model;

import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.util.concurrent.Future;

public class ProcessState {
    private StartedProcess startedProcess;
    private Process process;
    private Future<ProcessResult> processResult;


    public Future<ProcessResult> getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Future<ProcessResult> processResult) {
        this.processResult = processResult;
    }

    public ProcessState startedProcess(StartedProcess startedProcess) {
        this.startedProcess = startedProcess;
        return this;
    }

    public ProcessState process(Process process) {
        this.process = process;
        return this;
    }

    public ProcessState processResult(Future<ProcessResult> processResult) {
        this.processResult = processResult;
        return this;
    }

    public StartedProcess getStartedProcess() {
        return startedProcess;
    }

    public void setStartedProcess(StartedProcess processExecutor) {
        this.startedProcess = processExecutor;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
