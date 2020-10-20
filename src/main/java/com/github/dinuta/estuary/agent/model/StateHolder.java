package com.github.dinuta.estuary.agent.model;

import com.github.dinuta.estuary.agent.constants.DefaultConstants;

import java.io.File;

public class StateHolder {
    private final String LAST_COMMAND_FORMAT = new File(DefaultConstants.CMD_DETACHED_FOLDER).getAbsolutePath() + "/cmd_info_%s.json";
    private String lastCommand = String.format(LAST_COMMAND_FORMAT, "_");

    public void setLastCommand(String id) {
        this.lastCommand = String.format(LAST_COMMAND_FORMAT, id);
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public StateHolder lastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
        return this;
    }

    public String getLastCommandFormat() {
        return LAST_COMMAND_FORMAT;
    }
}
