package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.component.CommandRunner;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "estuary-agent")
@RestController
@Slf4j
public class CommandParallelApiController implements CommandParallelApi {
    private final HttpServletRequest request;
    private final CommandRunner commandRunner;
    private final ClientRequest clientRequest;
    private final About about;

    @Autowired
    public CommandParallelApiController(CommandRunner commandRunner, ClientRequest clientRequest,
                                        About about, HttpServletRequest request) {
        this.commandRunner = commandRunner;
        this.clientRequest = clientRequest;
        this.about = about;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandsParallelPost(@Parameter(description = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) throws IOException {
        String commandsStripped = commands.replace("\r\n", "\n").stripLeading().stripTrailing();
        List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                .stream().map(elem -> elem.stripLeading().stripTrailing()).collect(Collectors.toList());

        log.debug("Executing commands: " + commandsList);
        CommandDescription commandDescription;
        try {
            commandDescription = commandRunner.runCommandsParallel(commandsList.toArray(new String[0]));
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
}
