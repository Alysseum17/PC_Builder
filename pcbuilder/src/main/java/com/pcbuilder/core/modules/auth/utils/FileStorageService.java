package com.pcbuilder.core.modules.auth.utils;

import com.pcbuilder.core.modules.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileStorageService {
    private final Path fileStorageLocation;
    private final long maxFileSize;
    private final String[] allowedFileTypes;

    public FileStorageService(
            @Value("${file.upload.dir}") String uploadDir,
            @Value("${file.upload.max-size}") long maxFileSize,
            @Value("${file.upload.allowed-types}") String[] allowedFileTypes) {

        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
        this.allowedFileTypes = allowedFileTypes;
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage directory created at: {} ", this.fileStorageLocation);
        }catch (Exception e) {
            log.error("Could not create the directory where the uploaded files will be stored.", e);
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    public String storeFile(MultipartFile file) {

    }

    public void validateFile(MultipartFile file) {
       if(file.isEmpty()){
           throw new FileStorageException("Failed to store empty file.");
       }
       if(file.getSize() > maxFileSize) {
           throw new FileStorageException("File size exceeds the maximum allowed limit of " + maxFileSize + " bytes.");
         }
       if()

    }

    private String getFileExtension(String fileName) {
        if(fileName.lastIndexOf(".") == -1 || fileName.lastIndexOf(".") == 0) {
            return "";
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
    }

}
