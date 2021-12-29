package com.github.estuaryoss.agent.model;

public enum ExecutionStatus {
    RUNNING("running"),
    FINISHED("finished"),
    QUEUED("queued");

    private final String status;

    private ExecutionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
