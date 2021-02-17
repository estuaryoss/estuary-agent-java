package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.dinuta.estuary.agent.component.About;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.component.CommandRunner;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.exception.ApiException;
import com.github.dinuta.estuary.agent.exception.YamlConfigException;
import com.github.dinuta.estuary.agent.model.ConfigDescriptor;
import com.github.dinuta.estuary.agent.model.ProcessInfo;
import com.github.dinuta.estuary.agent.model.StateHolder;
import com.github.dinuta.estuary.agent.model.YamlConfig;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import com.github.dinuta.estuary.agent.utils.Base64FilePath;
import com.github.dinuta.estuary.agent.utils.ProcessUtils;
import com.github.dinuta.estuary.agent.utils.YamlConfigParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.github.dinuta.estuary.agent.utils.ProcessUtils.getParentProcessForDetachedCmd;

@Api(tags = {"estuary-agent"})
@RestController
public class CommandDetachedApiController implements CommandDetachedApi {

    private static final Logger log = LoggerFactory.getLogger(CommandDetachedApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private CommandRunner commandRunner;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private EnvApiController envApiController;

    @Autowired
    private StateHolder stateHolder;

    @Autowired
    private About about;

    @Autowired
    public CommandDetachedApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandDetachedDelete(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.NOT_IMPLEMENTED.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.NOT_IMPLEMENTED.getCode()))
                .description(ApiResponseMessage.getMessage(ApiResponseCode.NOT_IMPLEMENTED.getCode()))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ApiResponse> commandDetachedGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String testInfoFilename = stateHolder.getLastCommand();
        log.debug("Reading content from file: " + testInfoFilename);

        File testInfo = new File(testInfoFilename);
        CommandDescription commandDescription = new CommandDescription();

        try {
            if (!testInfo.exists())
                writeContentInFile(testInfo, commandDescription);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode()));
        }

        try (InputStream in = new FileInputStream(testInfo)) {
            commandDescription = objectMapper.readValue(IOUtils.toString(in, "UTF-8"), CommandDescription.class);
            commandDescription = streamOutAndErr(commandDescription);
            commandDescription.setProcesses(ProcessUtils.getProcesses());
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode()));
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

    private CommandDescription streamOutAndErr(CommandDescription commandDescription) {
        CommandDescription finalCommandDescription = commandDescription;
        Base64FilePath base64FilePath = new Base64FilePath();
        Set<String> commandKeys = commandDescription.getCommands().keySet();
        commandKeys.forEach(cmd -> {
            String output = "";
            String error = "";
            try (
                    InputStream isOut = new FileInputStream(
                            base64FilePath.getEncodedFileNameInBase64(cmd, stateHolder.getLastCommandId(), ".out"));
                    InputStream isErr = new FileInputStream(
                            base64FilePath.getEncodedFileNameInBase64(cmd, stateHolder.getLastCommandId(), ".err"))
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

    public ResponseEntity<ApiResponse> commandDetachedIdGet(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String testInfoFilename = String.format(stateHolder.getLastCommandFormat(), id);
        log.debug("Reading content from file: " + testInfoFilename);

        CommandDescription commandDescription;
        try (InputStream is = new FileInputStream(testInfoFilename)) {
            String fileContent = IOUtils.toString(is, "UTF-8");
            commandDescription = objectMapper.readValue(fileContent, CommandDescription.class);
            commandDescription = streamOutAndErr(commandDescription);
            commandDescription.setProcesses(ProcessUtils.getProcesses());
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode()));
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

    public ResponseEntity<ApiResponse> commandDetachedIdDelete(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");

        try {
            ProcessInfo parentProcessInfo = getParentProcessForDetachedCmd(id);
            List<ProcessHandle> children = parentProcessInfo.getChildren();
            ProcessUtils.killProcess(parentProcessInfo);
            if (children != null) ProcessUtils.killChildrenProcesses(children);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.COMMAND_DETACHED_STOP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_DETACHED_STOP_FAILURE.getCode())));
        } catch (InterruptedException e) {
            throw new ApiException(ApiResponseCode.COMMAND_DETACHED_STOP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_DETACHED_STOP_FAILURE.getCode())));
        } catch (TimeoutException e) {
            throw new ApiException(ApiResponseCode.COMMAND_DETACHED_STOP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_DETACHED_STOP_FAILURE.getCode())));
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

    public ResponseEntity<ApiResponse> commandDetachedIdPost(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String commandContent, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        File testInfo = new File(String.format(stateHolder.getLastCommandFormat(), id));
        CommandDescription commandDescription = CommandDescription.builder()
                .started(true)
                .finished(false)
                .id(id)
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

            List<String> argumentsList = new ArrayList<>();
            argumentsList.add("--cid=" + id);
            argumentsList.add("--enableStreams=true");
            argumentsList.add("--args=\"" + String.join(";;", commandsList.toArray(new String[0])) + "\"");

            log.debug("Sending args: " + argumentsList.toString());
            commandRunner.runStartCommandInBackground(argumentsList);
            stateHolder.setLastCommand(id);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.COMMAND_DETACHED_START_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_DETACHED_START_FAILURE.getCode()), id));
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

    public ResponseEntity<ApiResponse> commandDetachedIdPostYaml(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other in yaml format.", required = true) @Valid @RequestBody String commandContent, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        List<String> commandsList;
        File testInfo = new File(String.format(stateHolder.getLastCommandFormat(), id));
        YAMLMapper mapper = new YAMLMapper();
        CommandDescription commandDescription = CommandDescription.builder()
                .started(true)
                .finished(false)
                .id(id)
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
            apiResponse = envApiController.envPost(objectMapper.writeValueAsString(yamlConfig.getEnv()), token);
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
            List<String> argumentsList = new ArrayList<>();
            argumentsList.add("--cid=" + id);
            argumentsList.add("--enableStreams=true");
            argumentsList.add("--args=\"" + String.join(";;", commandsList.toArray(new String[0])) + "\"");

            log.debug("Sending args: " + argumentsList.toString());
            commandRunner.runStartCommandInBackground(argumentsList);
            stateHolder.setLastCommand(id);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.COMMAND_DETACHED_START_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_DETACHED_START_FAILURE.getCode()), id));
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

    private void writeContentInFile(File testInfo, CommandDescription commandDescription) throws IOException {
        FileWriter fileWriter = new FileWriter(testInfo);
        fileWriter.write(objectMapper.writeValueAsString(commandDescription));
        fileWriter.flush();
        fileWriter.close();
    }
}
