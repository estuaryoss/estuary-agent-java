package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(value = "processes", description = "processes API")
@RequestMapping(value = "")
public interface ProcessApi {

    @ApiOperation(value = "Gets the processes identified by process name", nickname = "getProcessesWithName", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get process list identified by name success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Get process list identified by name failure", response = ApiResponse.class)})
    @RequestMapping(value = "/processes/{process_name}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> getProcessesWithName(@ApiParam(value = "The name of the process", required = true) @PathVariable("process_name") String processName) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Gets the process list from the system", nickname = "getProcesses", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get all system processes success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Get all system processes failure", response = ApiResponse.class)})
    @RequestMapping(value = "/processes",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> getProcesses() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
