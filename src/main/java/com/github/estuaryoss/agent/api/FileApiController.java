package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.*;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Api(tags = {"estuary-agent"})
@RestController
@Slf4j
public class FileApiController implements FileApi {
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private About about;

    @Autowired
    private StorageService storageService;

    @Autowired
    public FileApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<? extends Object> fileGet(@ApiParam(value = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        String accept = request.getHeader("Accept");
        String headerName = "File-Path";

        log.debug(headerName + " Header: " + filePath);
        if (filePath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), headerName));
        }

        Resource resource;
        try {
            resource = storageService.loadAsResource(filePath);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_FILE_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode()));
        }

        return ResponseEntity.ok()
                .body(resource);
    }

    public ResponseEntity<? extends Object> fileGetQParam(@RequestParam(name = "filePath", required = false) String filePath) {
        String accept = request.getHeader("Accept");

        log.debug("Reading file: " + filePath);
        if (filePath == null) {
            throw new ApiException(ApiResponseCode.QUERY_PARAM_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.QUERY_PARAM_NOT_PROVIDED.getCode()), QParamConstants.FILE_PATH_Q_PARAM));
        }

        Resource resource;
        try {
            resource = storageService.loadAsResource(filePath);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_FILE_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode()));
        }

        return ResponseEntity.ok()
                .body(resource);
    }

    public ResponseEntity<ApiResponse> filePut(@ApiParam(value = "The content of the file") @Valid @RequestBody(required = false) byte[] content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath) {
        String accept = request.getHeader("Accept");
        String headerName = "File-Path";

        log.debug(headerName + " Header: " + filePath);
        if (filePath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), headerName));
        }

        try {
            storageService.store(content, filePath);
            log.info(String.format("Stored file at '%s'", filePath));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.UPLOAD_FILE_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.UPLOAD_FILE_FAILURE.getCode()));
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> filesPut(@RequestPart("files") MultipartFile[] files, @RequestParam(value = "folderPath", required = false) String folderPath) {
        String accept = request.getHeader("Accept");
        final String fPath = folderPath != null ? folderPath : DefaultConstants.UPLOADS_FOLDER;

        log.debug(String.format("Saving files at '%s'", fPath));

        Arrays.stream(files).forEach(file -> {
            try {
                String filePath = fPath + File.separator + file.getOriginalFilename();
                storageService.store(file, filePath);
                log.info(String.format("Stored file '%s' at '%s'", file.getName(), filePath));
            } catch (IOException e) {
                throw new ApiException(ApiResponseCode.UPLOAD_FILE_FAILURE_NAME.getCode(),
                        String.format(ApiResponseMessage.getMessage(ApiResponseCode.UPLOAD_FILE_FAILURE_NAME.getCode()),
                                file.getName(), fPath));
            }
        });

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> filePost(@ApiParam(value = "The content of the file") @Valid @RequestBody(required = false) byte[] content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath) {
        return filePut(content, filePath);
    }

}
