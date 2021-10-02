package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Api(value = "file", description = "the file API")
@RequestMapping(value = "")
public interface FileApi {

    @ApiOperation(value = "Gets the content of the file", nickname = "fileGet", notes = "", response = Object.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The content of the file in plain text, success", response = Object.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Failure, the file content could not be read", response = Object.class)})
    @RequestMapping(value = "/file",
            produces = {"application/json", "application/octet-stream", "text/plain"},
            method = RequestMethod.GET)
    default ResponseEntity<? extends Object> fileGet(@ApiParam(value = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Gets the content of the file, through query param", nickname = "fileGetQParam", notes = "", response = Object.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The content of the file in plain text, success", response = Object.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Failure, the file content could not be read", response = Object.class)})
    @RequestMapping(value = "/files",
            produces = {"application/json", "application/octet-stream", "text/plain"},
            method = RequestMethod.GET)
    default ResponseEntity<? extends Object> fileGetQParam(@RequestParam(name = "filePath") String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Uploads a file no mater the format. Binary or raw", nickname = "filePut", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The content of the file was uploaded successfully", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Failure, the file content could not be uploaded", response = ApiResponse.class)})
    @RequestMapping(value = "/file",
            produces = {"application/json", "text/plain"},
            consumes = {"application/json", "multipart/form-data", "application/x-www-form-urlencoded", "application/octet-stream", "text/plain"},
            method = RequestMethod.PUT)
    default ResponseEntity<ApiResponse> filePut(@ApiParam(value = "The content of the file") @Valid @RequestBody byte[] content, @ApiParam(value = "The path where the file to be saved", required = true) @RequestHeader(value = "File-Path", required = true) String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Uploads multiple files. Binary or raw", nickname = "filesPut", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The files were uploaded successfully", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Failure, the files could not be uploaded", response = ApiResponse.class)})
    @RequestMapping(value = "/files",
            produces = {"application/json"},
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> filesPut(@ApiParam(value = "The files to be uploaded") @RequestPart("files") MultipartFile[] files, @ApiParam(value = "The folder path where the file to be saved", required = true) @RequestParam(value = "folderPath", required = false) String folderPath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Uploads a file no mater the format. Binary or raw", nickname = "filePost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The content of the file was uploaded successfully", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Failure, the file content could not be uploaded", response = ApiResponse.class)})
    @RequestMapping(value = "/file",
            produces = {"application/json", "text/plain"},
            consumes = {"application/json", "multipart/form-data", "application/x-www-form-urlencoded", "application/octet-stream", "text/plain"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> filePost(@ApiParam(value = "The content of the file") @Valid @RequestBody byte[] content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
