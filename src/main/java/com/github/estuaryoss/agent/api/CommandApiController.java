package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.component.CommandRunner;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.entity.Command;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.ConfigDescriptor;
import com.github.estuaryoss.agent.model.ExecutionStatus;
import com.github.estuaryoss.agent.model.YamlConfig;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.service.DbService;
import com.github.estuaryoss.agent.utils.ProcessUtils;
import com.github.estuaryoss.agent.utils.YamlConfigParser;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "estuary-agent")
@RestController
@Slf4j
public class CommandApiController implements CommandApi {
    private final int RUNNING_COMMAND_HISTORY_LENGTH = 1_000;
    private final int FINISHED_COMMAND_HISTORY_LENGTH = 5_000;
    private final int ALL_COMMAND_HISTORY_LENGTH = 6_000;

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final EnvApiController envApiController;
    private final CommandRunner commandRunner;
    private final ClientRequest clientRequest;
    private final DbService dbService;
    private final About about;

    @Autowired
    public CommandApiController(ObjectMapper objectMapper, EnvApiController envApiController, CommandRunner commandRunner,
                                ClientRequest clientRequest, @Nullable DbService dbService, About about,
                                HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.envApiController = envApiController;
        this.commandRunner = commandRunner;
        this.clientRequest = clientRequest;
        this.dbService = dbService;
        this.about = about;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandGetAll() {
        String accept = request.getHeader("Accept");

        List<Command> allCommands = new ArrayList<>();
        if (dbService != null) allCommands = dbService.getCommands(ALL_COMMAND_HISTORY_LENGTH);


        log.debug("Dumping all commands from the database");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(allCommands)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandsGetAllByStatus(@PathVariable(name = "status", required = true) String status,
                                                              @RequestParam(name = "limit", required = false) String limit) {
        String accept = request.getHeader("Accept");
        Long queryLimit = Long.valueOf(RUNNING_COMMAND_HISTORY_LENGTH);
        if (limit != null) {
            try {
                queryLimit = Long.valueOf(limit);
            } catch (NumberFormatException e) {
                log.debug(String.format("Received invalid limit number '%s'\n", limit) + ExceptionUtils.getMessage(e));
            }
        }

        List<Command> commandsByStatus = new ArrayList<>();
        if (dbService != null) commandsByStatus = dbService.getCommands(status, queryLimit);

        log.debug("Dumping all running commands from the database");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(commandsByStatus)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandDeleteAll() {
        String accept = request.getHeader("Accept");
        log.debug("Killing all processes associated with active commands");
        List<Command> runningCommands = new ArrayList<>();

        if (dbService != null)
            runningCommands = dbService.getCommands(ExecutionStatus.RUNNING.getStatus(), RUNNING_COMMAND_HISTORY_LENGTH);
        log.debug(String.format("Running commands number: %s", runningCommands.size()));

        runningCommands.forEach(activeCommand -> {
            try {
                ProcessUtils.killProcessAndChildren(activeCommand.getPid());
            } catch (Exception e) {
                throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                        ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode()));
            }
        });

        if (dbService != null)
            runningCommands = dbService.getCommands(ExecutionStatus.RUNNING.getStatus(), RUNNING_COMMAND_HISTORY_LENGTH);
        log.debug(String.format("Running commands number: %s", runningCommands.size()));

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(runningCommands)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandDeleteByPid(@PathVariable(name = "pid", required = true) String pid) {
        String accept = request.getHeader("Accept");
        log.debug("Killing command associated with process id: " + pid);

        long processId;
        try {
            processId = Long.valueOf(pid);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.ILLEGAL_VALUE_EXCEPTION.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.ILLEGAL_VALUE_EXCEPTION.getCode()), pid));
        }

        try {
            ProcessUtils.killProcessAndChildren(processId);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode()));
        }
        List<Command> runningCommands = new ArrayList<>();
        if (dbService != null)
            dbService.getCommands(ExecutionStatus.RUNNING.getStatus(), RUNNING_COMMAND_HISTORY_LENGTH);
        log.debug(String.format("Running commands number: %s", runningCommands.size()));

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(runningCommands)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandsPost(@Parameter(description = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) throws IOException {
        String accept = request.getHeader("Accept");
        String commandsStripped = commands.replace("\r\n", "\n").strip();
        List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                .stream().map(elem -> elem.strip()).collect(Collectors.toList());

        log.debug("Executing commands: " + commandsList);
        CommandDescription commandDescription;
        try {
            commandDescription = commandRunner.runCommands(commandsList.toArray(new String[0]));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode()));
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(commandDescription)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandsPostYaml(@Parameter(description = "Commands to run in yaml format", required = true) @Valid @RequestBody String commands) throws IOException {
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

        log.debug("Executing commands: " + commandsList);
        configDescriptor.setYamlConfig(yamlConfig);
        try {
            configDescriptor.setDescription(commandRunner.runCommands(commandsList.toArray(new String[0])));
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode()));
        }

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
