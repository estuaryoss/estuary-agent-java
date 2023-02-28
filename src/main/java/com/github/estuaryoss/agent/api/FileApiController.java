package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.*;
import com.github.estuaryoss.agent.entity.FileTransfer;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.service.DbService;
import com.github.estuaryoss.agent.service.StorageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.estuaryoss.agent.constants.HeaderConstants.FILE_PATH;
import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FILE_NAME_MAX_SIZE;
import static com.github.estuaryoss.agent.constants.HibernateJpaConstants.FILE_PATH_MAX_SIZE;
import static com.github.estuaryoss.agent.utils.StringUtils.trimString;

@Tag(name = "estuary-agent")
@RestController
@Slf4j
public class FileApiController implements FileApi {
    private final int FILE_TRANSFER_HISTORY_MAX_LENGTH = 100;
    private final HttpServletRequest request;
    private final ClientRequest clientRequest;
    private final About about;
    private final DbService dbService;
    private final StorageService storageService;


    @Autowired
    public FileApiController(ClientRequest clientRequest, About about, @Nullable DbService dbService,
                             StorageService storageService, HttpServletRequest request) {
        this.clientRequest = clientRequest;
        this.about = about;
        this.dbService = dbService;
        this.storageService = storageService;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> fileRead(@Parameter(description = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        String accept = request.getHeader("Accept");

        log.debug(FILE_PATH + " Header: " + filePath);
        if (filePath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), FILE_PATH));
        }

        Resource resource;
        try {
            resource = storageService.loadAsResource(filePath);
            File file = new File(filePath);
            if (dbService != null) {
                dbService.saveFileTransfer(FileTransfer.builder()
                        .type(FileTransferType.DOWNLOAD.getType())
                        .sourceFileName(trimString(file.getName(), FILE_PATH_MAX_SIZE))
                        .sourceFilePath(trimString(file.getAbsolutePath(), FILE_PATH_MAX_SIZE))
                        .fileSize(resource.contentLength())
                        .dateTime(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                        .build());
            }
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_FILE_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode()));
        }

        String fileContent;
        try (InputStream inputStream = resource.getInputStream()) {
            fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_FILE_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode()));
        }

        return new ResponseEntity<ApiResponse>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(fileContent)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    @SneakyThrows
    public ResponseEntity<Resource> fileDownload(@Parameter(description = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        String accept = request.getHeader("Accept");

        log.debug(FILE_PATH + " Header: " + filePath);
        if (filePath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), FILE_PATH));
        }

        Resource resource;
        File file;
        try {
            resource = storageService.loadAsResource(filePath);
            file = new File(filePath);
            if (dbService != null) {
                dbService.saveFileTransfer(FileTransfer.builder()
                        .type(FileTransferType.DOWNLOAD.getType())
                        .sourceFileName(trimString(file.getName(), FILE_PATH_MAX_SIZE))
                        .sourceFilePath(trimString(file.getAbsolutePath(), FILE_PATH_MAX_SIZE))
                        .fileSize(resource.contentLength())
                        .dateTime(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                        .build());
            }
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_FILE_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode()));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.valueOf("application/octet-stream"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    public ResponseEntity<ApiResponse> filePut(@Parameter(description = "The content of the file") @Valid @RequestBody(required = false) byte[] content, @Parameter(description = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath) {
        String accept = request.getHeader("Accept");

        log.debug(FILE_PATH + " Header: " + filePath);
        if (filePath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), FILE_PATH));
        }

        try {
            storageService.store(content, filePath);
            File file = new File(filePath);
            if (dbService != null) {
                dbService.saveFileTransfer(FileTransfer.builder()
                        .type(FileTransferType.UPLOAD.getType())
                        .targetFileName(trimString(file.getName(), FILE_NAME_MAX_SIZE))
                        .targetFilePath(trimString(file.getAbsolutePath(), FILE_PATH_MAX_SIZE))
                        .targetFolder(trimString(file.getParent(), FILE_PATH_MAX_SIZE))
                        .fileSize(content != null ? Long.valueOf(content.length) : 0L)
                        .dateTime(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                        .build());
            }
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

    public ResponseEntity<ApiResponse> filesGet(@RequestParam(name = "limit", required = false) String limit) {
        Long queryLimit = Long.valueOf(FILE_TRANSFER_HISTORY_MAX_LENGTH);
        if (limit != null) {
            try {
                queryLimit = Long.valueOf(limit);
            } catch (NumberFormatException e) {
                log.debug(String.format("Received invalid limit number '%s'\n", limit) + ExceptionUtils.getMessage(e));
            }
        }

        List<FileTransfer> fileTransfers = new ArrayList<>();
        if (dbService != null) {
            fileTransfers = dbService.getFileTransfers(queryLimit);
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(fileTransfers)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> filesPut(@RequestPart("files") MultipartFile[] files, @RequestHeader(value = "Folder-Path", required = false) String folderPath) {
        String accept = request.getHeader("Accept");
        final String fPath = folderPath != null ? folderPath : DefaultConstants.UPLOADS_FOLDER;

        log.debug(String.format("Saving files at '%s'", fPath));

        Arrays.stream(files).forEach(file -> {
            try {
                String filePath = fPath + File.separator + file.getOriginalFilename();
                storageService.store(file, filePath);
                if (dbService != null) {
                    dbService.saveFileTransfer(FileTransfer.builder()
                            .type(FileTransferType.UPLOAD.getType())
                            .targetFileName(trimString(file.getOriginalFilename(), FILE_NAME_MAX_SIZE))
                            .sourceFileName(trimString(file.getOriginalFilename(), FILE_NAME_MAX_SIZE))
                            .targetFilePath(trimString(filePath, FILE_PATH_MAX_SIZE))
                            .fileSize(file.getSize())
                            .targetFolder(trimString(fPath, FILE_PATH_MAX_SIZE))
                            .dateTime(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                            .build());
                }
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

    public ResponseEntity<ApiResponse> filePost(@Parameter(description = "The content of the file") @Valid @RequestBody(required = false) byte[] content, @Parameter(description = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath) {
        return filePut(content, filePath);
    }

}
