package com.github.dinuta.estuary.agent.model;

import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

public class ProcessState {
    private StartedProcess startedProcess;
    private Process process;
    private Future<ProcessResult> processResult;
    private InputStream inputStream;
    private OutputStream errOutputStream;

    public ProcessState inputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public ProcessState errOutputStream(OutputStream errOutputStream) {
        this.errOutputStream = errOutputStream;
        return this;
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

    public void closeInputStream() throws IOException {
        if (inputStream != null) inputStream.close();
    }

    public void closeErrOutputStream() throws IOException {
        if (errOutputStream != null) errOutputStream.close();
    }

    public void closeStreams() throws IOException {
        closeErrOutputStream();
        closeInputStream();
    }

    public Future<ProcessResult> getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Future<ProcessResult> processResult) {
        this.processResult = processResult;
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

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getErrOutputStream() {
        return errOutputStream;
    }

    public void setErrOutputStream(OutputStream errOutputStream) {
        this.errOutputStream = errOutputStream;
    }
}
