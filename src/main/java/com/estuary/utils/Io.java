package com.estuary.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class Io {

    public static void createZipFile(File folderToZip, String outputZipPath) throws IOException {
        try (ZipArchiveOutputStream archive = new ZipArchiveOutputStream(new FileOutputStream(outputZipPath))) {
            Files.walk(folderToZip.toPath()).forEach(p -> {
                File file = p.toFile();
                if (!file.isDirectory()) {
                    ZipArchiveEntry entry = new ZipArchiveEntry(file, file.toString());
                    try (FileInputStream fis = new FileInputStream(file)) {
                        archive.putArchiveEntry(entry);
                        IOUtils.copy(fis, archive);
                        archive.closeArchiveEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            archive.finish();
        } catch (IOException e) {
            throw e;
        }
    }
}
