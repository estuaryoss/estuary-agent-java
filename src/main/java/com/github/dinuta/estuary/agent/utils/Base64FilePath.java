package com.github.dinuta.estuary.agent.utils;

import java.util.Base64;

import static com.github.dinuta.estuary.agent.constants.DefaultConstants.BACKGROUND_COMMANDS_STREAMS_FOLDER;

public class Base64FilePath {
    public String getEncodedFileNameInBase64(String command, String commandId, String termination) {
        return BACKGROUND_COMMANDS_STREAMS_FOLDER + "/" + Base64.getEncoder().encodeToString(command.getBytes()) + "_" + commandId + termination;
    }
}
