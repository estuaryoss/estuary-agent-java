package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.component.CommandRunner;
import com.github.estuaryoss.agent.component.ProcessHolder;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.ConfigDescriptor;
import com.github.estuaryoss.agent.model.ProcessState;
import com.github.estuaryoss.agent.model.YamlConfig;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.utils.ProcessUtils;
import com.github.estuaryoss.agent.utils.YamlConfigParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = {"estuary-agent"})
@RestController
public class CommandApiController implements CommandApi {

    private static final Logger log = LoggerFactory.getLogger(CommandApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private EnvApiController envApiController;

    @Autowired
    private CommandRunner commandRunner;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private ProcessHolder processHolder;

    @Autowired
    private About about;

    @Autowired
    public CommandApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandGetAll() {
        String accept = request.getHeader("Accept");

        log.debug("Dumping all in memory processes associated with active commands");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(processHolder.dumpAll())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }


    public ResponseEntity<ApiResponse> commandDeleteAll() {
        String accept = request.getHeader("Accept");
        log.debug("Killing all processes associated with active commands");
        log.debug(String.format("Active processes number: %s", processHolder.getAll().size()));

        for (Map.Entry<ProcessState, String> entry : processHolder.getAll().entrySet()) {
            ProcessState processState = entry.getKey();
            if (processState.getProcess().isAlive()) {
                try {
                    ProcessUtils.killProcessAndChildren(processState);
                } catch (Exception e) {
                    throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                            ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode()));
                }
            }
        }

        processHolder.clearAll();

        log.debug(String.format("Active processes number: %s", processHolder.getAll().size()));
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(processHolder.dumpAll())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) throws IOException {
        String accept = request.getHeader("Accept");
        String commandsStripped = commands.replace("\r\n", "\n").strip();
        List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                .stream().map(elem -> elem.strip()).collect(Collectors.toList());

        log.debug("Executing commands: " + commandsList.toString());
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(commandRunner.runCommands(commandsList.toArray(new String[0])))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandPostYaml(@ApiParam(value = "Commands to run in yaml format", required = true) @Valid @RequestBody String commands) throws IOException {
        String accept = request.getHeader("Accept");
        String commandsStripped = commands.strip();
        List<String> commandsList;
        YAMLMapper yamlMapper = new YAMLMapper();
        ResponseEntity<ApiResponse> apiResponse;
        ConfigDescriptor configDescriptor = new ConfigDescriptor();
        YamlConfig yamlConfig;

        try {
            yamlConfig = yamlMapper.readValue(commandsStripped, YamlConfig.class);
            apiResponse = envApiController.envPost(objectMapper.writeValueAsString(yamlConfig.getEnv()));
            yamlConfig.setEnv((Map<String, String>) apiResponse.getBody().getDescription());
            commandsList = new YamlConfigParser().getCommandsList(yamlConfig).stream()
                    .map(elem -> elem.strip()).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.INVALID_YAML_CONFIG.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.INVALID_YAML_CONFIG.getCode()));
        }

        log.debug("Executing commands: " + commandsList.toString());
        configDescriptor.setYamlConfig(yamlConfig);
        configDescriptor.setDescription(commandRunner.runCommands(commandsList.toArray(new String[0])));
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(configDescriptor)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }
}
