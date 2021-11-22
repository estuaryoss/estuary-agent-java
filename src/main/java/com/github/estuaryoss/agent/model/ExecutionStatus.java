package com.github.estuaryoss.agent.model;

public enum ExecutionStatus {
    IN_PROGRESS("in progress"),
    FINISHED("finished");

    private final String status;

    private ExecutionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
