package com.pcbuilder.core.modules.user.utils;

import com.pcbuilder.core.modules.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileStorageService {
    private final Path fileStorageLocation;
    private final long maxFileSize;
    private final Set<String> allowedFileExtensions;

    public FileStorageService(
            @Value("${file.upload.dir}") String uploadDir,
            @Value("${file.upload.max-size}") long maxFileSize,
            @Value("${file.upload.allowed-extensions}") Set<String> allowedFileExtensions) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
        this.allowedFileExtensions = allowedFileExtensions;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Could not create the directory where the uploaded files will be stored.", ex);
            throw new FileStorageException("Could not create upload directory");
        }
    }

    public String storeFile(MultipartFile file) {
        validateFile(file);
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        try {
            if(originalFilename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
            }
            copyToStorage(file.getInputStream(), fileName);
            return fileName;
        }catch(IOException e) {
            throw new FileStorageException("Could not store file " + originalFilename + ". Please try again!");
        }
    }
    public String storeStream(InputStream inputStream, String extension) {
        String filename = UUID.randomUUID().toString() + "." + extension;
        try {
            copyToStorage(inputStream, filename);
            return filename;
        } catch (Exception e) {
            log.error("Failed to store processed image", e);
            throw new FileStorageException("Failed to store processed image");
        }
    }

    private void copyToStorage(InputStream inputStream, String filename) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", filename);
        } catch (IOException e) {
            log.error("Could not write file to disk: {}", filename, e);
            throw new FileStorageException("Could not write file to disk");
        }
    }

    public List<String> storeFiles(List<MultipartFile> files) {
        return files.stream().map(this::storeFile).collect(Collectors.toList());
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error(ex.getMessage(), ex);
            throw new FileStorageException("File not found ");

        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("Could not delete file: " + fileName, ex);
            throw new FileStorageException("Could not delete file: " + fileName);
        }
    }

    public String getContentType(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            return Files.probeContentType(filePath);
        } catch (IOException ex) {
            log.error("Could not determine file type: " + fileName, ex);
            return "application/octet-stream";
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot upload empty file");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileStorageException(
                    String.format("File size exceeds maximum allowed size of %d bytes", maxFileSize)
            );
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!allowedFileExtensions.contains(extension.toLowerCase())) {
            throw new FileStorageException(
                    "File extension not allowed: " + extension
            );
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
