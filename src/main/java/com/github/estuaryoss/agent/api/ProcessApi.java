package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Tag(name = "processes", description = "processes API")
@RequestMapping(value = "")
public interface ProcessApi {

    @Operation(description = "Gets the processes identified by process name", summary = "getProcessesWithName", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get process list identified by name success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Get process list identified by name failure")
    @RequestMapping(value = "/processes/{process_name}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> getProcessesWithName(@Parameter(description = "The name of the process", required = true) @PathVariable("process_name") String processName) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Gets the process list from the system", summary = "getProcesses", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get all system processes success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Get all system processes failure")
    @RequestMapping(value = "/processes",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> getProcesses() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
