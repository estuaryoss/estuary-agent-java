package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.IOException;

@Api(value = "command", description = "the command API")
@RequestMapping(value = "")
public interface CommandApi {

    @ApiOperation(value = "Dumps the active commands that are running on the Agent.", nickname = "commandGetAll", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Dump active commands success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Dump active commands failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandActiveGetAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Dumps the finished commands on the Agent.", nickname = "commandFinishedGetAll", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Dump finished commands success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Dump finished commands failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsfinished",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandFinishedGetAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Stops all the active commands on the Agent by terminating their corresponding process", nickname = "commandDeleteAll", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "All commands process terminated success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "All commands process terminated failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDeleteAll() {
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
    @RequestMapping(value = "/commandsyaml",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsPostYaml(@ApiParam(value = "Commands to run described as yaml", required = true) @Valid @RequestBody String commands) throws IOException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
