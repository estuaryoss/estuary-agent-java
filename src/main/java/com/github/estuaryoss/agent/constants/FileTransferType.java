package com.github.estuaryoss.agent.constants;

public enum FileTransferType {
    DOWNLOAD("download"),
    UPLOAD("upload");

    private final String type;

    private FileTransferType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
