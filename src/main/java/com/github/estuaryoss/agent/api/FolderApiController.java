package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.FileTransferType;
import com.github.estuaryoss.agent.entity.FileTransfer;
import com.github.estuaryoss.agent.exception.ApiException;
import com.github.estuaryoss.agent.service.DbService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Api(tags = {"estuary-agent"})
@RestController
@Slf4j
public class FolderApiController implements FolderApi {
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private DbService dbService;

    @Autowired
    public FolderApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<? extends Object> folderGet(@ApiParam(value = "Target folder path to get as zip", required = false) @RequestHeader(value = "Folder-Path", required = false) String folderPath) {
        String accept = request.getHeader("Accept");
        String archiveNamePath = "archive.zip";
        String headerName = "Folder-Path";

        log.debug(headerName + " Header: " + folderPath);
        if (folderPath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), headerName));
        }

        File file;
        File sourceFolderPath;
        try {
            file = new File(archiveNamePath);
            sourceFolderPath = new File(folderPath);
            ZipUtil.pack(sourceFolderPath, file, name -> name);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode()), folderPath));
        }

        ByteArrayResource resource;
        try (InputStream in = new FileInputStream(archiveNamePath)) {
            resource = new ByteArrayResource(IOUtils.toByteArray(in));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode()), folderPath));
        }

        dbService.saveFileTransfer(FileTransfer.builder()
                .type(FileTransferType.DOWNLOAD.getType())
                .sourceFileName(sourceFolderPath.getName())
                .sourceFilePath(sourceFolderPath.getAbsolutePath())
                .targetFileName(file.getName())
                .targetFilePath(file.getAbsolutePath())
                .fileSize(resource.contentLength())
                .build());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.valueOf("application/zip"))
                .contentLength(file.length())
                .body(resource);
    }

}
