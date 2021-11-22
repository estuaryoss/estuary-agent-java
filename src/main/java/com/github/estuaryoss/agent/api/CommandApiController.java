package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.component.CommandRunner;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.entity.ActiveCommand;
import com.github.estuaryoss.agent.entity.FinishedCommand;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.ConfigDescriptor;
import com.github.estuaryoss.agent.model.YamlConfig;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.repository.FinishedCommandRepository;
import com.github.estuaryoss.agent.service.DbService;
import com.github.estuaryoss.agent.utils.ProcessUtils;
import com.github.estuaryoss.agent.utils.YamlConfigParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.COMMAND_MAX_SIZE;
import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FIELD_MAX_SIZE;
import static com.github.estuaryoss.agent.utils.StringUtils.trimString;

@Api(tags = {"estuary-agent"})
@RestController
@Slf4j
public class CommandApiController implements CommandApi {
    private final int COMMAND_HISTORY_MAX_LENGTH = 50;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private EnvApiController envApiController;

    @Autowired
    private CommandRunner commandRunner;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private DbService dbService;

    @Autowired
    private About about;

    @Autowired
    private FinishedCommandRepository finishedCommandRepository;

    @Autowired
    public CommandApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandGetAll() {
        String accept = request.getHeader("Accept");

        log.debug("Dumping all active commands from the database");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(dbService.getAllActiveCommands())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandFinishedGetAll() {
        String accept = request.getHeader("Accept");
        String limitString = request.getParameter("limit");
        Long limit = Long.valueOf(COMMAND_HISTORY_MAX_LENGTH);
        if (limitString != null) {
            try {
                limit = Long.valueOf(limitString);
            } catch (NumberFormatException e) {
                log.debug(String.format("Received invalid limit number '%s'\n", limit) + ExceptionUtils.getMessage(e));
            }
        }

        log.debug("Dumping all finished commands from the database");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(dbService.getFinishedCommands(limit))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandDeleteAll() {
        String accept = request.getHeader("Accept");
        log.debug("Killing all processes associated with active commands");
        log.debug(String.format("Active commands number: %s", dbService.getAllActiveCommands().size()));

        List<ActiveCommand> activeCommandList = dbService.getAllActiveCommands();
        activeCommandList.forEach(activeCommand -> {
            try {
                ProcessUtils.killProcessAndChildren(activeCommand.getPid());
            } catch (Exception e) {
                throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                        ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode()));
            }
        });

        dbService.clearAllActiveCommands();

        log.debug(String.format("Active commands number: %s", dbService.getAllActiveCommands().size()));
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(dbService.getAllActiveCommands())
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
        CommandDescription commandDescription;
        try {
            commandDescription = commandRunner.runCommands(commandsList.toArray(new String[0]));
        } catch (Exception e) {
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
        try {
            configDescriptor.setDescription(commandRunner.runCommands(commandsList.toArray(new String[0])));
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode()));
        }

        ((CommandDescription) configDescriptor.getDescription()).getCommands().forEach((command, commandStatus) -> {
            finishedCommandRepository.saveAndFlush(FinishedCommand.builder()
                    .command(trimString(command, COMMAND_MAX_SIZE))
                    .code(commandStatus.getDetails().getCode())
                    .out(trimString(commandStatus.getDetails().getOut(), FIELD_MAX_SIZE))
                    .err(trimString(commandStatus.getDetails().getErr(), FIELD_MAX_SIZE))
                    .startedAt(commandStatus.getStartedat())
                    .finishedAt(commandStatus.getFinishedat())
                    .duration(commandStatus.getDuration())
                    .pid(commandStatus.getDetails().getPid())
                    .build());
        });

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
