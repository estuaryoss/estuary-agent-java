package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.github.estuaryoss.agent.exception.YamlConfigException;
import com.github.estuaryoss.agent.model.*;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.model.api.CommandDetails;
import com.github.estuaryoss.agent.model.api.CommandStatus;
import com.github.estuaryoss.agent.service.DbService;
import com.github.estuaryoss.agent.utils.Base64FilePath;
import com.github.estuaryoss.agent.utils.ProcessUtils;
import com.github.estuaryoss.agent.utils.StringUtils;
import com.github.estuaryoss.agent.utils.YamlConfigParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FIELD_MAX_SIZE;
import static com.github.estuaryoss.agent.utils.ProcessUtils.getProcessInfoForPid;
import static com.github.estuaryoss.agent.utils.ProcessUtils.getProcessInfoForPidAndParent;

@Api(tags = {"estuary-agent"})
@RestController
@Slf4j
public class CommandsBackgroundApiController implements CommandsBackgroundApi {
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private CommandRunner commandRunner;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private EnvApiController envApiController;

    @Autowired
    private BackgroundStateHolder backgroundStateHolder;

    @Autowired
    private About about;

    @Autowired
    DbService dbService;

    @Autowired
    public CommandsBackgroundApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandDetachedDelete() {
        String accept = request.getHeader("Accept");

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

    public ResponseEntity<ApiResponse> commandDetachedGet() {
        String accept = request.getHeader("Accept");
        String lastCommandId = backgroundStateHolder.getLastCommandId();
        log.debug("Last command id was: " + lastCommandId);
        String commandDescriptionInfoFilename = backgroundStateHolder.getLastCommandFilename();
        log.debug("Reading content from file: " + commandDescriptionInfoFilename);

        File commandDescriptionInfo = new File(commandDescriptionInfoFilename);
        CommandDescription commandDescription = new CommandDescription();

        try {
            if (!commandDescriptionInfo.exists())
                writeContentInFile(commandDescriptionInfo, commandDescription);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode()));
        }

        try (InputStream in = new FileInputStream(commandDescriptionInfo)) {
            commandDescription = objectMapper.readValue(IOUtils.toString(in, "UTF-8"), CommandDescription.class);
            commandDescription = streamOutAndErr(commandDescription);
            commandDescription.setProcesses(getProcessInfoForPidAndParent(commandDescription.getPid(), false));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode()));
        }

        fillCommandStatus(commandDescription, lastCommandId);

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

    public ResponseEntity<ApiResponse> commandDetachedIdGet(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        String commandDescriptionInfoFilename = String.format(backgroundStateHolder.getCommandFilenameFormat(), id);
        log.debug("Reading content from file: " + commandDescriptionInfoFilename);

        CommandDescription commandDescription;
        try (InputStream is = new FileInputStream(commandDescriptionInfoFilename)) {
            String fileContent = IOUtils.toString(is, "UTF-8");
            commandDescription = objectMapper.readValue(fileContent, CommandDescription.class);
            commandDescription = streamOutAndErr(commandDescription);
            commandDescription.setProcesses(getProcessInfoForPidAndParent(commandDescription.getPid(), false));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode()));
        }

        fillCommandStatus(commandDescription, id);

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

    public ResponseEntity<ApiResponse> commandDetachedIdDelete(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");

        String testInfoFilename = String.format(backgroundStateHolder.getCommandFilenameFormat(), id);
        log.debug("Reading content from file: " + testInfoFilename);

        CommandDescription commandDescription;
        try (InputStream is = new FileInputStream(testInfoFilename)) {
            String fileContent = IOUtils.toString(is, "UTF-8");
            commandDescription = objectMapper.readValue(fileContent, CommandDescription.class);
            commandDescription = streamOutAndErr(commandDescription);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode()));
        }

        try {
            List<ProcessInfo> processInfoList = getProcessInfoForPid(commandDescription.getPid(), false);
            if (processInfoList.size() == 0) {
                throw new ApiException(ApiResponseCode.COMMAND_PROCESS_DOES_NOT_EXIST.getCode(),
                        String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_PROCESS_DOES_NOT_EXIST.getCode()),
                                String.valueOf(commandDescription.getPid())));
            }
            List<ProcessHandle> children = processInfoList.get(0).getChildren();
            ProcessUtils.killProcess(processInfoList.get(0));
            if (children != null) ProcessUtils.killChildrenProcesses(children);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode())));
        } catch (InterruptedException e) {
            throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode())));
        } catch (TimeoutException e) {
            throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode())));
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandDetachedIdPost(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String commandContent) {
        String accept = request.getHeader("Accept");
        String commandId = StringUtils.trimString(id, FIELD_MAX_SIZE);
        File testInfo = new File(String.format(backgroundStateHolder.getCommandFilenameFormat(), commandId));
        CommandDescription commandDescription = CommandDescription.builder()
                .started(true)
                .finished(false)
                .id(commandId)
                .pid(ProcessHandle.current().pid())
                .build();

        if (commandContent == null) {
            throw new ApiException(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.getCode())));
        }

        try {
            writeContentInFile(testInfo, commandDescription);
            String commandsStripped = commandContent.replace("\r\n", "\n").strip();
            List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                    .stream().map(elem -> elem.strip()).collect(Collectors.toList());
            log.debug("Executing commands: " + commandsList.toString());

            commandRunner.runCommandsInBackground(commandsList, commandId);
            backgroundStateHolder.setLastCommandId(id);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.COMMAND_START_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_START_FAILURE.getCode()), id));
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())))
                .description(id)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.ACCEPTED);
    }

    public ResponseEntity<ApiResponse> commandDetachedIdPostYaml(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other in yaml format.", required = true) @Valid @RequestBody String commandContent) {
        String accept = request.getHeader("Accept");
        List<String> commandsList;
        File testInfo = new File(String.format(backgroundStateHolder.getCommandFilenameFormat(), id));
        YAMLMapper mapper = new YAMLMapper();
        CommandDescription commandDescription = CommandDescription.builder()
                .startedat(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .started(true)
                .finished(false)
                .pid(ProcessHandle.current().pid())
                .build();
        ResponseEntity<ApiResponse> apiResponse;
        ConfigDescriptor configDescriptor = new ConfigDescriptor();
        YamlConfig yamlConfig;

        if (commandContent == null) {
            throw new ApiException(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.getCode()));
        }

        String commandsStripped = commandContent.replace("\r\n", "\n").strip();
        try {
            yamlConfig = mapper.readValue(commandsStripped, YamlConfig.class);
            apiResponse = envApiController.envPost(objectMapper.writeValueAsString(yamlConfig.getEnv()));
            yamlConfig.setEnv((Map<String, String>) apiResponse.getBody().getDescription());
            commandsList = new YamlConfigParser().getCommandsList(yamlConfig).stream()
                    .map(elem -> elem.strip()).collect(Collectors.toList());
        } catch (JsonProcessingException | YamlConfigException e) {
            throw new ApiException(ApiResponseCode.INVALID_YAML_CONFIG.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.INVALID_YAML_CONFIG.getCode()));
        }

        try {
            writeContentInFile(testInfo, commandDescription);
            log.debug("Executing commands: " + commandsList.toString());
            commandRunner.runCommandsInBackground(commandsList, StringUtils.trimString(id, FIELD_MAX_SIZE));
            backgroundStateHolder.setLastCommandId(id);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.COMMAND_START_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_START_FAILURE.getCode()), id));
        }

        configDescriptor.setYamlConfig(yamlConfig);
        configDescriptor.setDescription(id);
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())))
                .description(configDescriptor)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    private CommandDescription streamOutAndErr(CommandDescription commandDescription) {
        CommandDescription finalCommandDescription = commandDescription;
        Base64FilePath base64FilePath = new Base64FilePath();
        Set<String> commandKeys = commandDescription.getCommands().keySet();
        commandKeys.forEach(cmd -> {
            String output = "";
            String error = "";
            try (
                    InputStream isOut = new FileInputStream(
                            base64FilePath.getEncodedFileNameInBase64(cmd, backgroundStateHolder.getLastCommandId(), ".out"));
                    InputStream isErr = new FileInputStream(
                            base64FilePath.getEncodedFileNameInBase64(cmd, backgroundStateHolder.getLastCommandId(), ".err"))
            ) {
                output = IOUtils.toString(isOut, "UTF-8");
                error = IOUtils.toString(isErr, "UTF-8");
            } catch (Exception e) {
                log.debug(ExceptionUtils.getStackTrace(e));
            }

            finalCommandDescription.getCommands().get(cmd).getDetails().setOut(output);
            finalCommandDescription.getCommands().get(cmd).getDetails().setErr(error);
        });

        return finalCommandDescription;
    }

    private void writeContentInFile(File testInfo, CommandDescription commandDescription) throws IOException {
        @Cleanup FileWriter fileWriter = new FileWriter(testInfo);
        fileWriter.write(objectMapper.writeValueAsString(commandDescription));
        fileWriter.flush();
    }

    private void fillCommandStatus(CommandDescription commandDescription, String commandId) {
        LinkedHashMap<String, CommandStatus> commands = new LinkedHashMap<>();
        List<ActiveCommand> activeCommands = dbService.getAllActiveCommandsByCommandId(commandId);
        List<FinishedCommand> finishedCommands = dbService.getAllFinishedCommandsByCommandId(commandId);

        activeCommands.forEach(activeCommand -> {
            CommandDetails commandDetails = CommandDetails.builder()
                    .pid(activeCommand.getPid())
                    .build();
            CommandStatus commandStatus = CommandStatus.builder()
                    .startedat(activeCommand.getStartedAt())
                    .status(ExecutionStatus.IN_PROGRESS.getStatus())
                    .details(commandDetails)
                    .build();
            commands.put(activeCommand.getCommand(), commandStatus);
        });

        finishedCommands.forEach(finishedCommand -> {
            CommandDetails commandDetails = CommandDetails.builder()
                    .pid(finishedCommand.getPid())
                    .args(finishedCommand.getCommand().split(" "))
                    .build();
            CommandStatus commandStatus = CommandStatus.builder()
                    .startedat(finishedCommand.getStartedAt())
                    .finishedat(finishedCommand.getFinishedAt())
                    .status(ExecutionStatus.FINISHED.getStatus())
                    .duration(finishedCommand.getDuration())
                    .details(commandDetails)
                    .build();
            commands.put(finishedCommand.getCommand(), commandStatus);
        });

        if (activeCommands.size() == 0 && finishedCommands.size() != 0) {
            commandDescription.setFinished(true);
            commandDescription.setStarted(false);
        }

        commandDescription.setCommands(commands);
    }
}
