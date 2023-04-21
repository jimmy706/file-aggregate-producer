package com.assessment.fileaggregateproducer.scheduler;

import com.assessment.fileaggregateproducer.model.ReadFileStatus;
import com.assessment.fileaggregateproducer.model.SaleInformation;
import com.assessment.fileaggregateproducer.model.SalesFileInformation;
import com.assessment.fileaggregateproducer.producer.SalesFileProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SaleFilesReader {
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy hh:mm a");

    private static final Logger LOG = LoggerFactory.getLogger(SaleFilesReader.class);

    private final ObjectMapper objectMapper;

    private final SalesFileProducer salesFileProducer;

    private final FileArchiveHandler fileArchiveHandler;

    @Autowired
    SaleFilesReader(ObjectMapper objectMapper, SalesFileProducer salesFileProducer, FileArchiveHandler fileArchiveHandler) {
        this.objectMapper = objectMapper;
        this.salesFileProducer = salesFileProducer;
        this.fileArchiveHandler = fileArchiveHandler;
    }

    public boolean readSaleFile(File saleFile) {
        if (!saleFile.isFile()) {
            LOG.warn("Cannot read this file");
            return false;
        }
        try (CSVReader csvReader = new CSVReader(new FileReader(saleFile))) {
            csvReader.readNext(); // Read the header
            String[] record;
            List<SaleInformation> saleInformations = new ArrayList<>();
            while ((record = csvReader.readNext()) != null) {
                Date datetime = format.parse(record[0]);
                String product = record[1];
                int quantity = Integer.parseInt(record[2]);
                double price = Double.parseDouble(record[3]);

                SaleInformation saleInformation = new SaleInformation(datetime, product, quantity, price);
                saleInformations.add(saleInformation);
                LOG.info("Successfully read the file. File data: {}", saleInformation);
            }
            SalesFileInformation salesData = new SalesFileInformation(saleFile.getName(), new Date(), saleInformations);
            salesFileProducer.sendSalesData(objectMapper.writeValueAsString(salesData));

            LOG.info("Move read file to archive directory");
            return true;
        } catch (IOException | CsvValidationException | ParseException e) {
            LOG.warn("Cannot process this file due to error: {}", e.getMessage());
            return false;
        }
    }

    public Map<ReadFileStatus, Integer> readSaleFiles(List<File> files) {
        Map<ReadFileStatus, Integer> result = new HashMap<>(Map.ofEntries(
                Map.entry(ReadFileStatus.SUCCESS, 0),
                Map.entry(ReadFileStatus.FAILED, 0)
        ));
        files.forEach(f -> {
            boolean isSuccess = readSaleFile(f);
            if (isSuccess) {
                result.put(ReadFileStatus.SUCCESS, result.get(ReadFileStatus.SUCCESS) + 1);
                try {
                    LOG.info("Moving read file to archive folder");
                    boolean successArchiveFile = fileArchiveHandler.removeReadFileAndMoveToArchiveFolder(f);
                    if (!successArchiveFile) {
                        LOG.warn("Failed to move {} to archive folder", f.getPath());
                    }
                } catch (IOException e) {
                    f.delete();
                    LOG.warn("Failed to move the file to archive folder due to {}", e.getMessage());
                }
            } else {
                result.put(ReadFileStatus.FAILED, result.get(ReadFileStatus.FAILED) + 1);
            }
        });

        return result;
    }
}
