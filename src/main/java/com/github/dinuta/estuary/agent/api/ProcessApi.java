package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(value = "processes", description = "processes API")
@RequestMapping(value = "")
public interface ProcessApi {

    @ApiOperation(value = "Gets the process list from the system", nickname = "getProcesses", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get system processes success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Get system processes failure", response = ApiResponse.class)})
    @RequestMapping(value = "/processes",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> getProcesses() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
