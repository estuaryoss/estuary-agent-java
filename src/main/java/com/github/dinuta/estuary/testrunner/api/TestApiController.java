package com.github.dinuta.estuary.testrunner.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.testrunner.constants.About;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseConstants;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseMessage;
import com.github.dinuta.estuary.testrunner.constants.DateTimeConstants;
import com.github.dinuta.estuary.testrunner.model.api.ApiResponse;
import com.github.dinuta.estuary.testrunner.model.api.CommandDescription;
import com.github.dinuta.estuary.testrunner.utils.CommandRunner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"estuary-testrunner"})
@Controller
public class TestApiController implements TestApi {

    private static final Logger log = LoggerFactory.getLogger(TestApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public TestApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> testDelete(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<ApiResponse>(new ApiResponse()
                .code(ApiResponseConstants.NOT_IMPLEMENTED)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.NOT_IMPLEMENTED))
                .description(ApiResponseMessage.getMessage(ApiResponseConstants.NOT_IMPLEMENTED))
                .name(About.getAppName())
                .version(About.getVersion())
                .time(LocalDateTime.now().format(DateTimeConstants.PATTERN)), HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ApiResponse> testGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String testInfoFilename = new File(".").getAbsolutePath() + "/testinfo.json";
        File testInfo = new File(testInfoFilename);
        CommandDescription commandDescription = new CommandDescription();
        FileWriter fileWriter = null;
        try {
            if (!testInfo.exists())
                writeContentInFile(testInfo, commandDescription);
            Path path = Paths.get(testInfoFilename);
            String fileContent = String.join("\n", Files.readAllLines(path));
            commandDescription = objectMapper.readValue(fileContent, CommandDescription.class);
        } catch (Exception e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse()
                    .code(ApiResponseConstants.GET_TEST_INFO_FAILURE)
                    .message(ApiResponseMessage.getMessage(ApiResponseConstants.GET_TEST_INFO_FAILURE))
                    .description(ExceptionUtils.getStackTrace(e))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .time(LocalDateTime.now().format(DateTimeConstants.PATTERN)), HttpStatus.OK);
        }

        return new ResponseEntity<ApiResponse>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description(commandDescription)
                .name(About.getAppName())
                .version(About.getVersion())
                .time(LocalDateTime.now().format(DateTimeConstants.PATTERN)), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> testIdPost(@ApiParam(value = "Test id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String testFileContent, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String testInfoFilename = new File(".").getAbsolutePath() + "/testinfo.json";
        File testInfo = new File(testInfoFilename);
        CommandDescription commandDescription = new CommandDescription()
                .started(true)
                .finished(false)
                .id(id);

        if (testFileContent == null) {
            return new ResponseEntity<ApiResponse>(new ApiResponse()
                    .code(ApiResponseConstants.EMPTY_REQUEST_BODY_PROVIDED)
                    .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.EMPTY_REQUEST_BODY_PROVIDED)))
                    .description(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.EMPTY_REQUEST_BODY_PROVIDED)))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .time(LocalDateTime.now().format(DateTimeConstants.PATTERN)), HttpStatus.NOT_FOUND);
        }

        try {
            writeContentInFile(testInfo, commandDescription);
            CommandRunner commandRunner = new CommandRunner();
            String commandsStripped = testFileContent.replace("\r\n", "\n").stripLeading().stripTrailing();
            List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                    .stream().map(elem -> elem.stripLeading().stripTrailing()).collect(Collectors.toList());
            List<String> startPyArgumentsList = new ArrayList<>();
            startPyArgumentsList.add(id);
            startPyArgumentsList.add(String.join(";", commandsList.toArray(new String[0])));

            commandRunner.runStartCommandDetached(startPyArgumentsList);
        } catch (Exception e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse()
                    .code(ApiResponseConstants.TEST_START_FAILURE)
                    .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.TEST_START_FAILURE)))
                    .description(ExceptionUtils.getStackTrace(e))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .time(LocalDateTime.now().format(DateTimeConstants.PATTERN)), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<ApiResponse>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)))
                .description(id)
                .name(About.getAppName())
                .version(About.getVersion())
                .time(LocalDateTime.now().format(DateTimeConstants.PATTERN)), HttpStatus.OK);
    }

    private void writeContentInFile(File testInfo, CommandDescription commandDescription) throws IOException {
        FileWriter fileWriter = new FileWriter(testInfo);
        fileWriter.write(objectMapper.writeValueAsString(commandDescription));
        fileWriter.flush();
        fileWriter.close();
    }
}
