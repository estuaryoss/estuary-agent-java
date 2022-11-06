package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Tag(name = "command", description = "the command API")
@RequestMapping(value = "")
public interface CommandApi {

    @Operation(description = "Dumps running and finished commands on the Agent.", summary = "commandGetAll", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dump commands success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Dump commands failure")
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandGetAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Dumps all commands on the Agent by status", summary = "commandsGetAllByStatus", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dump commands by status success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Dump commands by status failure")
    @RequestMapping(value = "/commands/{status}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandsGetAllByStatus(@PathVariable(name = "status", required = true) String status, @RequestParam(name = "limit", required = false) String limit) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @Operation(description = "Stops all the running commands on the Agent by terminating their corresponding process",
            summary = "commandDeleteAll", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All commands process terminated success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "All commands process terminated failure")
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDeleteAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Stops running Command on the Agent by pid", summary = "commandDeleteByPid", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Terminate command process success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Terminate command process failure")
    @RequestMapping(value = "/commands/{pid}",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDeleteByPid(@PathVariable(name = "pid", required = true) String pid) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Starts multiple commands in blocking mode sequentially. Set the client timeout at needed value.",
            summary = "commandPost", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Command start success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Command start failure")
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsPost(@Parameter(description = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) throws IOException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Starts multiple commands in blocking mode sequentially. The commands are described in yaml format. " +
            "Set the client timeout at needed value.", summary = "commandPost", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Command start success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Command start failure")
    @RequestMapping(value = "/commands/yaml",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsPostYaml(@Parameter(description = "Commands to run described as yaml", required = true) @Valid @RequestBody String commands) throws IOException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
