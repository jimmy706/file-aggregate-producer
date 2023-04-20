package com.assessment.fileaggregateproducer.scheduler;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class FileArchiveHandler {
    private final ResourceLoader resourceLoader;

    @Autowired
    public FileArchiveHandler(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public boolean removeReadFileAndMoveToArchiveFolder(File file) throws IOException {
        if (!file.isFile()) {
            return false;
        }
        Resource archvieResource = resourceLoader.getResource("classpath:archive");
        File archiveFolder = archvieResource.getFile();

        Path archivePath = Paths.get(archiveFolder.getPath() + File.separator + file.getName());
        File archiveFile = archivePath.toFile();
        if (archiveFile.exists()) {
            archiveFile.delete();
        }
        return file.renameTo(archivePath.toFile());
    }
}
