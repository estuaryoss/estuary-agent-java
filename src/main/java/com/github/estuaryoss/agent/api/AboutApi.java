package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(value = "about", description = "the about API")
@RequestMapping(value = "")
public interface AboutApi {

    @ApiOperation(value = "Information about the application", nickname = "aboutGet", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Prints the name and version of the application.", response = ApiResponse.class)})
    @RequestMapping(value = "/about",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> aboutGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
