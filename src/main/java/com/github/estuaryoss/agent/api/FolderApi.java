package com.github.estuaryoss.agent.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Tag(name = "folder", description = "the folder API")
@RequestMapping(value = "")
public interface FolderApi {

    @Operation(description = "Gets the folder as zip archive. Useful to get test results folder", summary = "folderGet",
            tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The content of the folder as zip archive")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failure, the content of the folder could not be obtained")
    @RequestMapping(value = "/folder",
            produces = {"application/zip", "application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<Resource> folderGet(@Parameter(description = "Target folder path to get as zip", required = false) @RequestHeader(value = "Folder-Path", required = false) String folderPath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
