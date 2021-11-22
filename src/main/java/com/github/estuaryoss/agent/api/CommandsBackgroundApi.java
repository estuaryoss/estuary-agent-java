package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Api(value = "commandsbackground", description = "the command detached API")
@RequestMapping(value = "")
public interface CommandsBackgroundApi {

    @ApiOperation(value = "Stops all commands that were previously started in background mode", nickname = "commandDetachedDelete", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "command stop success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "command stop failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDetachedDelete() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Stops all commands that were previously started in background mode", nickname = "commandsInBackgroundDelete", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "command stop success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "command stop failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsbackground",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandsInBackgroundDelete() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Gets information about the last command started in background", nickname = "commandDetachedGet", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get command detached info success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Get command detached info failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandDetachedGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Gets information about the last commands started in background", nickname = "commandsInBackgroundGet", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get command info success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Get command info failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsbackground",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandsInBackgroundGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Gets information about the command identified by id started in background", nickname = "commandDetachedIdGet", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get command in background info success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Get command in background info failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached/{id}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandDetachedIdGet(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Gets information about the commands identified by id started in background", nickname = "commandsInBackgroundGetById", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get command in background info success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Get command in background info failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsbackground/{id}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandsInBackgroundGetById(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts the shell commands in background and sequentially", nickname = "commandDetachedIdPost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached/{id}",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandDetachedIdPost(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String commandContent) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts the shell commands in background and sequentially", nickname = "commandsInBackgroundGetById", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsbackground/{id}",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsInBackgroundGetById(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String commandContent) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts the commands in background and sequentially. The commands are described by yaml.", nickname = "commandDetachedIdPostYaml", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetachedyaml/{id}",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandDetachedIdPostYaml(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other described as yaml", required = true) @Valid @RequestBody String commandContent) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts the commands in background and sequentially. The commands are described by yaml.", nickname = "commandsInBackgroundByIdPostYaml", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsbackgroundyaml/{id}",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsInBackgroundByIdPostYaml(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other described as yaml", required = true) @Valid @RequestBody String commandContent) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Deletes the associated processes of the shell commands started in background", nickname = "commandDetachedIdDelete", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Command delete success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Command delete failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached/{id}",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDetachedIdDelete(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Deletes the associated processes of the shell commands started in background", nickname = "commandsInBackgroundDeleteById", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Command delete success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Command delete failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsbackground/{id}",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandsInBackgroundDeleteById(@ApiParam(value = "Command id set by the user", required = true) @PathVariable("id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
