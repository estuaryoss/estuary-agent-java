package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Tag(name = "file", description = "the file API")
@RequestMapping(value = "")
public interface FileApi {

    @Operation(description = "Gets the content of the file", summary = "fileRead", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The content of the file in plain text, success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failure, the file content could not be read")
    @RequestMapping(value = "/file/read",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> fileRead(@Parameter(description = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Gets the file as an attachment", summary = "fileDownload", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The file download, success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failure, the file could not be downloaded")
    @RequestMapping(value = "/file/download",
            produces = {"application/octet-stream", "application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<Resource> fileDownload(@Parameter(description = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Uploads a file no mater the format. Binary or raw", summary = "filePut", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The content of the file was uploaded successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failure, the file content could not be uploaded")
    @RequestMapping(value = "/file",
            produces = {"application/json", "text/plain"},
            consumes = {"application/json", "multipart/form-data", "application/x-www-form-urlencoded", "application/octet-stream", "text/plain"},
            method = RequestMethod.PUT)
    default ResponseEntity<ApiResponse> filePut(@Parameter(description = "The content of the file") @Valid @RequestBody byte[] content, @Parameter(description = "The path where the file to be saved", required = true) @RequestHeader(value = "File-Path", required = true) String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Uploads multiple files. Binary or raw", summary = "filesPut", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The files were uploaded successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failure, the files could not be uploaded")
    @RequestMapping(value = "/files",
            produces = {"application/json"},
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            method = {RequestMethod.PUT, RequestMethod.POST})
    default ResponseEntity<ApiResponse> filesPut(@Parameter(description = "The files to be uploaded") @RequestPart("files") MultipartFile[] files, @Parameter(description = "The folder path where the file to be saved", required = true) @RequestHeader(value = "Folder-Path", required = false) String folderPath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Gets the history of file transfers: uploads or downloads", summary = "filesGet", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The file transfer history was retrieved successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failure, the file history could not be retrieved")
    @RequestMapping(value = "/files",
            produces = {"application/json"},
            method = {RequestMethod.GET})
    default ResponseEntity<ApiResponse> filesGet(@RequestParam(name = "limit") String limit) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(description = "Uploads a file no mater the format. Binary or raw", summary = "filePost", tags = {"estuary-agent",})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The content of the file was uploaded successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failure, the file content could not be uploaded")
    @RequestMapping(value = "/file",
            produces = {"application/json", "text/plain"},
            consumes = {"application/json", "multipart/form-data", "application/x-www-form-urlencoded", "application/octet-stream", "text/plain"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> filePost(@Parameter(description = "The content of the file") @Valid @RequestBody byte[] content, @Parameter(description = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
