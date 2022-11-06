package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Tag(name = "ping")
@RequestMapping(value = "")
public interface PingApi {

    @Operation(description = "Ping endpoint which replies with pong", summary = "pingGet", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ping endpoint which replies with pong. Useful when checking the alive status of the service")
    @RequestMapping(value = "/ping",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> pingGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
