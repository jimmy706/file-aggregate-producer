package com.assessment.fileaggregateproducer.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class SalesFileController {
    private final ResourceLoader resourceLoader;

    @Autowired
    public SalesFileController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostMapping(value = "/sales-files", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Validate the uploaded file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        Resource resource = resourceLoader.getResource("classpath:sales");
        // Save the uploaded file to the resources/sales folder
        String fileName = file.getOriginalFilename();
        File destFile = new File(resource.getFile().getPath() + File.separator + fileName);
        file.transferTo(destFile);

        // Optionally, perform additional processing on the uploaded file

        // Return a success response
        return ResponseEntity.ok("File uploaded successfully");
    }
}
