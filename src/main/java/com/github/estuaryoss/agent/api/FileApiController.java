package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.constants.QParamConstants;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.time.LocalDateTime;

@Api(tags = {"estuary-agent"})
@RestController
public class FileApiController implements FileApi {

    private static final Logger log = LoggerFactory.getLogger(FileApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private About about;

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

        ByteArrayResource resource;
        try (InputStream in = new FileInputStream(new File(filePath))) {
            resource = new ByteArrayResource(IOUtils.toByteArray(in));
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

        ByteArrayResource resource;
        try (InputStream in = new FileInputStream(filePath)) {
            resource = new ByteArrayResource(IOUtils.toByteArray(in));
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

        try (OutputStream outputStream = new FileOutputStream(new File(filePath))) {
            org.apache.commons.io.IOUtils.write(content, outputStream);
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

    public ResponseEntity<ApiResponse> filePost(@ApiParam(value = "The content of the file") @Valid @RequestBody(required = false) byte[] content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath) {
        return filePut(content, filePath);
    }

}
