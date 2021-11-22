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
public interface CommandParallelApi {

    @ApiOperation(value = "Starts multiple commands in blocking mode parallel. Set the client timeout at needed value.", nickname = "commandPost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandsparallel",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandsParallelPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) throws IOException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
