package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Api(value = "command", description = "the command API")
@RequestMapping(value = "")
public interface CommandApi {

    @ApiOperation(value = "Dumps running and finished commands on the Agent.", nickname = "commandGetAll", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Dump commands success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Dump commands failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandGetAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Dumps all commands on the Agent by status", nickname = "commandsGetAllByStatus", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Dump commands by status success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Dump commands by status failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands/{status}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandsGetAllByStatus(@PathVariable(name = "status", required = true) String status, @RequestParam(name = "limit", required = false) String limit) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Stops all the running commands on the Agent by terminating their corresponding process", nickname = "commandDeleteAll", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "All commands process terminated success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "All commands process terminated failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDeleteAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Stops running Command on the Agent by pid", nickname = "commandDeleteByPid", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Terminate command process success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Terminate command process failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands/{pid}",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDeleteByPid(@PathVariable(name = "pid", required = true) String pid) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts multiple commands in blocking mode sequentially. Set the client timeout at needed value.", nickname = "commandPost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) throws IOException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts multiple commands in blocking mode sequentially. The commands are described in yaml format. " +
            "Set the client timeout at needed value.", nickname = "commandPost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Commands start failure", response = ApiResponse.class)
    })
    @RequestMapping(value = "/commands/yaml",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsPostYaml(@ApiParam(value = "Commands to run described as yaml", required = true) @Valid @RequestBody String commands) throws IOException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
