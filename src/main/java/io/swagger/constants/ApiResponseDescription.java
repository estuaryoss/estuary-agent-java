package io.swagger.constants;

import java.util.HashMap;

import static io.swagger.constants.ApiResponseConstants.*;

public class ApiResponseDescription {
    private static HashMap<String, String> description = new HashMap<>();

    static {
        description.put(SUCCESS, "success");
        description.put(JINJA2_RENDER_FAILURE, "jinja2 render failed");
        description.put(GET_FILE_FAILURE, "Getting file or folder from the estuary testrunner service container failed");
        description.put(TEST_START_FAILURE, "Starting test id %s failed");
        description.put(TEST_STOP_FAILURE, "Stopping running test %s failed");
        description.put(GET_CONTAINER_FILE_FAILURE, "Getting %s from the container %s failed");
        description.put(GET_CONTAINER_FILE_FAILURE_IS_DIR, "Getting %s from the container %s failed. It is a directory, not a file.");
        description.put(GET_CONTAINER_ENV_VAR_FAILURE, "Getting env var %s from the container failed.");
        description.put(MISSING_PARAMETER_POST, "Body parameter \"%s\" sent in request missing. Please include parameter. E.g. {\"parameter\"); \"value\"}");
        description.put(GET_CONTAINER_TEST_INFO_FAILURE, "Failed to get test info.");
        description.put(FOLDER_ZIP_FAILURE, "Failed to zip folder %s.");
        description.put(EMPTY_REQUEST_BODY_PROVIDED, "Empty request body provided.");
        description.put(UPLOAD_TEST_CONFIG_FAILURE, "Failed to upload test configuration.");
        description.put(HTTP_HEADER_NOT_PROVIDED, "Http header value not provided, '%s'");
        description.put(COMMAND_EXEC_FAILURE, "Starting command(s) failed");
        description.put(EXEC_COMMAND_NOT_ALLOWED, "'rm' commands are filtered. Command '%s' was not executed.");
        description.put(UNAUTHORIZED, "Unauthorized");
        description.put(SET_ENV_VAR_FAILURE, "Failed to set env vars \"%s\"");
        description.put(INVALID_JSON_PAYLOAD, "Invalid json body \"%s\"");
    }

    public static String getDescription(String apiResponseCode) {
        return description.get(apiResponseCode);
    }
}
