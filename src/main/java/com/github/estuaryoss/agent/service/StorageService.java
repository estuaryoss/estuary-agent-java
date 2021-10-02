package com.github.estuaryoss.agent.service;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;

@Service
public class StorageService {

    public void store(@Valid byte[] content, String filePath) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            org.apache.commons.io.IOUtils.write(content, outputStream);
        }
    }

    public void store(MultipartFile file, String filePath) throws IOException {
        store(file.getBytes(), filePath);
    }

    public ByteArrayResource loadAsResource(String filePath) throws IOException {
        ByteArrayResource resource;
        try (InputStream in = new FileInputStream(filePath)) {
            resource = new ByteArrayResource(IOUtils.toByteArray(in));
        }
        return resource;
    }
}
