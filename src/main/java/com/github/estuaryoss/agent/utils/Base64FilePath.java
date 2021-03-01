package com.github.estuaryoss.agent.utils;

import com.github.estuaryoss.agent.constants.DefaultConstants;

import java.util.Base64;

public class Base64FilePath {
    public String getEncodedFileNameInBase64(String command, String commandId, String termination) {
        return DefaultConstants.BACKGROUND_COMMANDS_STREAMS_FOLDER + "/" + Base64.getEncoder().encodeToString(command.getBytes()) + "_" + commandId + termination;
    }
}
