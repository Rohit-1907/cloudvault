package com.cloudvault.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

/**
 * Local File Storage Service (for development/testing)
 * Stores encrypted files on the local filesystem instead of cloud storage.
 * Perfect for development and can be swapped out for S3/Supabase later.
 */
@Slf4j
@Service
public class CloudStorageService {

    private static final String STORAGE_DIR = "cloudvault-storage";
    private static final String UPLOADS_DIR = "uploads";

    public CloudStorageService() {
        initializeStorageDirectory();
    }

    /**
     * Initialize local storage directory
     */
    private void initializeStorageDirectory() {
        try {
            Path storagePath = Paths.get(STORAGE_DIR, UPLOADS_DIR);
            Files.createDirectories(storagePath);
            log.info("Storage directory initialized: {}", storagePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create storage directory", e);
        }
    }

    /**
     * Upload encrypted file to local filesystem
     */
    public String uploadFile(byte[] fileData, String originalFileName) {
        try {
            String fileKey = generateFileKey(originalFileName);
            Path filePath = Paths.get(STORAGE_DIR, UPLOADS_DIR, fileKey);
            
            // Create parent directories if they don't exist
            Files.createDirectories(filePath.getParent());
            
            // Write file
            Files.write(filePath, fileData);
            
            log.info("File uploaded successfully to local storage: {} (Original: {})", fileKey, originalFileName);
            return fileKey;
        } catch (IOException e) {
            log.error("Failed to upload file to local storage: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to local storage", e);
        }
    }

    /**
     * Download encrypted file from local filesystem
     */
    public byte[] downloadFile(String fileKey) {
        try {
            Path filePath = Paths.get(STORAGE_DIR, UPLOADS_DIR, fileKey);
            
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + fileKey);
            }
            
            byte[] data = Files.readAllBytes(filePath);
            log.info("File downloaded from local storage: {}", fileKey);
            return data;
        } catch (IOException e) {
            log.error("Failed to download file from local storage: {}", e.getMessage());
            throw new RuntimeException("Failed to download file", e);
        }
    }

    /**
     * Delete file from local filesystem
     */
    public void deleteFile(String fileKey) {
        try {
            Path filePath = Paths.get(STORAGE_DIR, UPLOADS_DIR, fileKey);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted from local storage: {}", fileKey);
            } else {
                log.warn("File not found for deletion: {}", fileKey);
            }
        } catch (IOException e) {
            log.error("Failed to delete file from local storage: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Check if file exists in local storage
     */
    public boolean fileExists(String fileKey) {
        Path filePath = Paths.get(STORAGE_DIR, UPLOADS_DIR, fileKey);
        return Files.exists(filePath);
    }

    /**
     * Generate unique file key for storage
     */
    private String generateFileKey(String originalFileName) {
        String timestamp = System.currentTimeMillis() + "";
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFileName);
        return uuid + "-" + timestamp + extension;
    }

    /**
     * Extract file extension
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot);
        }
        return "";
    }

}
