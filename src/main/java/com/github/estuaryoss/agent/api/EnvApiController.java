package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.AppEnvironment;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "estuary-agent")
@RestController
@Slf4j
public class EnvApiController implements EnvApi {
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final AppEnvironment environment;
    private final ClientRequest clientRequest;
    private final About about;

    @Autowired
    public EnvApiController(ObjectMapper objectMapper, AppEnvironment appEnvironment, ClientRequest clientRequest,
                            About about, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.environment = appEnvironment;
        this.clientRequest = clientRequest;
        this.about = about;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> envEnvNameGet(@Parameter(description = "The name of the env var to get value from", required = true) @PathVariable("env_name") String envName) {
        String accept = request.getHeader("Accept");

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(environment.getEnvAndVirtualEnv().get(envName))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> envGet() {
        String accept = request.getHeader("Accept");

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(environment.getEnvAndVirtualEnv())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> envVirtualGet() {
        String accept = request.getHeader("Accept");

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(environment.getVirtualEnv())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> envSystemGet() {
        String accept = request.getHeader("Accept");

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(environment.getEnv())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> deleteEnvVars() {
        String accept = request.getHeader("Accept");

        environment.cleanVirtualEnv();

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(environment.getVirtualEnv())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }


    public ResponseEntity<ApiResponse> deleteEnvVar(@PathVariable String envVarName) {
        String accept = request.getHeader("Accept");

        environment.getVirtualEnv().remove(envVarName);

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(environment.getVirtualEnv())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> envPost(@Parameter(description = "List of env vars by key-value pair in JSON format", required = true) @Valid @RequestBody String envVars) {
        Map<String, String> envVarsToBeAdded;
        Map<String, String> virtualEnvVarsAdded;

        try {
            envVarsToBeAdded = objectMapper.readValue(envVars, LinkedHashMap.class);
            virtualEnvVarsAdded = environment.setVirtualEnvVars(envVarsToBeAdded);
        } catch (JsonProcessingException e) {
            throw new ApiException(ApiResponseCode.SET_ENV_VAR_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.SET_ENV_VAR_FAILURE.getCode()), envVars));
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(virtualEnvVarsAdded)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }
}
