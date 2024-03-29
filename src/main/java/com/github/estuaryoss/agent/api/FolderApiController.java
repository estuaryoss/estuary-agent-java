package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.*;
import com.github.estuaryoss.agent.entity.FileTransfer;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.service.DbService;
import com.github.estuaryoss.agent.service.StorageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.github.estuaryoss.agent.constants.HeaderConstants.FOLDER_PATH;

@Tag(name = "estuary-agent")
@RestController
@Slf4j
public class FolderApiController implements FolderApi {
    private final HttpServletRequest request;
    private final DbService dbService;
    private final StorageService storageService;

    @Autowired
    public FolderApiController(@Nullable DbService dbService, StorageService storageService, HttpServletRequest request) {
        this.dbService = dbService;
        this.storageService = storageService;
        this.request = request;
    }

    @SneakyThrows
    public ResponseEntity<Resource> folderGet(@Parameter(description = "Target folder path to get as zip", required = false) @RequestHeader(value = "Folder-Path", required = false) String folderPath) {
        String accept = request.getHeader("Accept");
        log.debug(FOLDER_PATH + " Header: " + folderPath);
        if (folderPath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), FOLDER_PATH));
        }

        File sourceFolderPath;
        File file;
        try {
            sourceFolderPath = new File(folderPath);
            file = new File(DefaultConstants.DOWNLOADS_FOLDER + File.separator + String.format("%s.zip", sourceFolderPath.getName()));

            ZipUtil.pack(sourceFolderPath, file, name -> name);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode()), folderPath));
        }

        Resource resource;
        try {
            resource = storageService.loadAsResource(file.getAbsolutePath());

            if (dbService != null) {
                dbService.saveFileTransfer(FileTransfer.builder()
                        .type(FileTransferType.DOWNLOAD.getType())
                        .sourceFileName(sourceFolderPath.getName())
                        .sourceFilePath(sourceFolderPath.getAbsolutePath())
                        .targetFileName(file.getName())
                        .targetFilePath(file.getAbsolutePath())
                        .fileSize(resource.contentLength())
                        .dateTime(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                        .build());
            }
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode()), folderPath));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.valueOf("application/zip"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

}
