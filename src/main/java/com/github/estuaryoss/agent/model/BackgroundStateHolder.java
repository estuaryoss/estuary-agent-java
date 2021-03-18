package com.github.estuaryoss.agent.model;

import java.io.File;

import static com.github.estuaryoss.agent.constants.DefaultConstants.BACKGROUND_COMMANDS_FOLDER;

public class BackgroundStateHolder {

    private final String LAST_COMMAND_FORMAT = new File(BACKGROUND_COMMANDS_FOLDER).getAbsolutePath() + "/cmd_info_%s.json";
    private String lastCommand = String.format(LAST_COMMAND_FORMAT, "_");
    private String lastCommandId = "_";

    public void setLastCommand(String id) {
        this.lastCommand = String.format(LAST_COMMAND_FORMAT, id);
        this.lastCommandId = id;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public String getLastCommandId() {
        return lastCommandId;
    }

    public String getLastCommandFormat() {
        return LAST_COMMAND_FORMAT;
    }
}
