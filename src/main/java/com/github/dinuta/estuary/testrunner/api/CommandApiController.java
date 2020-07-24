package com.github.dinuta.estuary.testrunner.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.testrunner.constants.About;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseConstants;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseMessage;
import com.github.dinuta.estuary.testrunner.model.api.ApiResponse;
import com.github.dinuta.estuary.testrunner.utils.CommandRunner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

@Api(tags = {"estuary-testrunner"})
@Controller
public class CommandApiController implements CommandApi {

    private static final Logger log = LoggerFactory.getLogger(CommandApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public CommandApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        CommandRunner commandRunner = new CommandRunner();
        String commandsStripped = commands.replace("\r\n", "\n").stripLeading().stripTrailing();
        List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                .stream().map(elem -> elem.stripLeading().stripTrailing()).collect(Collectors.toList());

        return new ResponseEntity<ApiResponse>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description(commandRunner.runCommands(commandsList.toArray(new String[0])))
                .name(About.getAppName())
                .version(About.getVersion())
                .time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))), HttpStatus.OK);
    }
}
