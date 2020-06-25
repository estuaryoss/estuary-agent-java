/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.13).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.estuary.api;

import com.estuary.model.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

@Api(value = "file", description = "the file API")
@RequestMapping(value = "")
public interface FileApi {

    @ApiOperation(value = "Gets the content of the file", nickname = "fileGet", notes = "", response = ApiResponse.class, tags = {"estuary-testrunner",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The content of the file in plain text, success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Failure, the file content could not be read", response = ApiResponse.class)})
    @RequestMapping(value = "/file",
            produces = {"application/json"},
            consumes = {"application/json", "application/octet-stream", "text/plain"},
            method = RequestMethod.GET)
    default ResponseEntity<? extends Object> fileGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token, @ApiParam(value = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Uploads a file no mater the format. Binary or raw", nickname = "filePut", notes = "", response = ApiResponse.class, tags = {"estuary-testrunner",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The content of the file was uploaded successfully", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Failure, the file content could not be uploaded", response = ApiResponse.class)})
    @RequestMapping(value = "/file",
            produces = {"application/json", "text/plain"},
            consumes = {"application/json", "application/x-www-form-urlencoded", "text/plain"},
            method = RequestMethod.PUT)
    default ResponseEntity<ApiResponse> filePut(@ApiParam(value = "The content of the file", required = true) @Valid @RequestBody String content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
