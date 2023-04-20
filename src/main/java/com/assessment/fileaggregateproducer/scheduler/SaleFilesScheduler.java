package com.assessment.fileaggregateproducer.scheduler;

import com.assessment.fileaggregateproducer.model.ReadFileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@EnableScheduling
public class SaleFilesScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(SaleFilesScheduler.class);

    private final ResourceLoader resourceLoader;

    private final SaleFilesReader saleFilesReader;

    private final FileArchiveHandler fileArchiveHandler;

    @Autowired
    SaleFilesScheduler(ResourceLoader resourceLoader, SaleFilesReader saleFilesReader, FileArchiveHandler fileArchiveHandler) {
        this.saleFilesReader = saleFilesReader;
        this.resourceLoader = resourceLoader;
        this.fileArchiveHandler = fileArchiveHandler;
    }

    @Scheduled(fixedDelay = 60000) // Run every minute
    public void schedullyReadSalesFiles() throws IOException {
        LOG.info("Started to read sale files");
        Resource resource = resourceLoader.getResource("classpath:sales");
        File salesDirectory = resource.getFile();
        if (!salesDirectory.exists()) {
            LOG.warn("Sales files not found");
            return;
        }
        List<File> salesFiles = List.of(salesDirectory.listFiles());

        Map<ReadFileStatus, Integer> readFileResult = saleFilesReader.readSaleFiles(salesFiles);
        LOG.info("Ended read sale files, number of file read: {}", salesFiles.size());
        LOG.info("Number of success file: {}", readFileResult.get(ReadFileStatus.SUCCESS));
        LOG.info("Number of failed file: {}", readFileResult.get(ReadFileStatus.FAILED));
    }
}
