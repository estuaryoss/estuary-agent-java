package com.github.estuaryoss.agent.model;

import java.io.File;

import static com.github.estuaryoss.agent.constants.DefaultConstants.BACKGROUND_COMMANDS_FOLDER;


public class BackgroundStateHolder {
    private final String COMMAND_FILENAME_FORMAT = new File(BACKGROUND_COMMANDS_FOLDER).getAbsolutePath() + "/cmd_info_%s.json";
    private String lastCommandFilename = String.format(COMMAND_FILENAME_FORMAT, "_");
    private String lastCommandId = "_";

    public void setLastCommandId(String id) {
        this.lastCommandId = id;
    }

    public String getLastCommandFilename() {
        return lastCommandFilename;
    }

    public String getLastCommandId() {
        return lastCommandId;
    }

    public String getCommandFilenameFormat() {
        return COMMAND_FILENAME_FORMAT;
    }
}
