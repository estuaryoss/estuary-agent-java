package com.github.estuaryoss.agent.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Tag(name = "about", description = "the about API")
@RequestMapping(value = "")
public interface AboutApi {

    @Operation(description = "Information about the application", summary = "aboutGet", tags = {"estuary-agent"})
    @ApiResponse(responseCode = "200", description = "Prints the name and version of the application.")
    @RequestMapping(value = "/about",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> aboutGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
