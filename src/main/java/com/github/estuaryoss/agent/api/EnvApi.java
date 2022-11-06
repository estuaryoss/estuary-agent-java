package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Tag(name = "env", description = "the env API")
@RequestMapping(value = "")
public interface EnvApi {

    @Operation(description = "Gets the environment variable value from the environment", summary = "envEnvNameGet", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get env var success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Get env var failure")
    @RequestMapping(value = "/env/{env_name}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> envEnvNameGet(@Parameter(description = "The name of the env var to get value from", required = true) @PathVariable("env_name") String envName) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @Operation(description = "Print all environment variables: system & virtual", summary = "envGet", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get all environment variables: system & virtual")
    @RequestMapping(value = "/env",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> envGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Print all system environment variables", summary = "envSystemGet", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get all system environment variables: system & virtual")
    @RequestMapping(value = "/env/system",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> envSystemGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Print all virtual environment variables", summary = "envVirtualGet", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of the virtual environment variables")
    @RequestMapping(value = "/env/virtual",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> envVirtualGet() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Deletes the custom defined env vars contained in the virtual environment", summary = "envDelete", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deletes the entire virtual env vars, but keeping system env vars.")
    @RequestMapping(value = "/env",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> envDelete() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Set environment variables", summary = "envPost", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Set environment variables success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Set environment variables failure")
    @RequestMapping(value = "/env",
            produces = {"application/json"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> envPost(@Parameter(description = "List of env vars by key-value pair in JSON format", required = true) @Valid @RequestBody String envVars) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
