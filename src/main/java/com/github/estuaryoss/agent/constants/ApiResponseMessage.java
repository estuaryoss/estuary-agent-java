package com.github.estuaryoss.agent.constants;

import java.util.HashMap;

public class ApiResponseMessage {
    private static final HashMap<Integer, String> message = new HashMap<>();

    static {
        message.put(ApiResponseCode.SUCCESS.getCode(), "Success");
        message.put(ApiResponseCode.JINJA2_RENDER_FAILURE.getCode(), "Jinja2 render failed");
        message.put(ApiResponseCode.GET_FILE_FAILURE.getCode(), "Getting file or folder from the estuary agent service failed");
        message.put(ApiResponseCode.COMMAND_START_FAILURE.getCode(), "Starting detached command with id %s failed");
        message.put(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(), "Stopping running detached commands failed");
        message.put(ApiResponseCode.COMMAND_PROCESS_DOES_NOT_EXIST.getCode(), "No process running on pid %s");
        message.put(ApiResponseCode.GET_FILE_FAILURE_IS_DIR.getCode(), "Getting %s failed. It is a directory.getCode(), not a file.");
        message.put(ApiResponseCode.GET_ENV_VAR_FAILURE.getCode(), "Getting env var %s failed.");
        message.put(ApiResponseCode.MISSING_PARAMETER_POST.getCode(), "Body parameter \"%s\" sent in request missing. Please include parameter. E.g. {\"parameter\"); \"value\"}");
        message.put(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(), "Failed to get detached command info.");
        message.put(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode(), "Failed to zip folder %s.");
        message.put(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.getCode(), "Empty request body provided.");
        message.put(ApiResponseCode.UPLOAD_FILE_FAILURE.getCode(), "Failed to upload file.");
        message.put(ApiResponseCode.UPLOAD_FILE_FAILURE_NAME.getCode(), "Failed to upload file '%s' in path '%s'.");
        message.put(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(), "Http header value not provided, '%s'");
        message.put(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode(), "Command execution failed");
        message.put(ApiResponseCode.EXEC_COMMAND_NOT_ALLOWED.getCode(), "'rm' commands are filtered. Command '%s' was not executed.");
        message.put(ApiResponseCode.UNAUTHORIZED.getCode(), "Unauthorized");
        message.put(ApiResponseCode.SET_ENV_VAR_FAILURE.getCode(), "Failed to set env vars \"%s\"");
        message.put(ApiResponseCode.INVALID_JSON_PAYLOAD.getCode(), "Invalid json body \"%s\"");
        message.put(ApiResponseCode.INVALID_YAML_CONFIG.getCode(), "Invalid yaml config");
        message.put(ApiResponseCode.QUERY_PARAM_NOT_PROVIDED.getCode(), "Query param '%s' not provided");
        message.put(ApiResponseCode.UNEXPECTED_EXCEPTION.getCode(), "Unexpected exception occurred");
        message.put(ApiResponseCode.ILLEGAL_VALUE_EXCEPTION.getCode(), "Illegal value '%s' received");
        message.put(ApiResponseCode.NOT_IMPLEMENTED.getCode(), "Not implemented");
    }

    public static String getMessage(int apiResponseCode) {
        return message.get(apiResponseCode);
    }
}
