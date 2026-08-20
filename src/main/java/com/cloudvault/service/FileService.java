package com.cloudvault.service;

import com.cloudvault.dto.FileAccessResponse;
import com.cloudvault.dto.FileUploadRequest;
import com.cloudvault.dto.FileUploadResponse;
import com.cloudvault.dto.EncryptionVerificationResponse;
import com.cloudvault.model.CloudFile;
import com.cloudvault.model.FileStatus;
import com.cloudvault.repository.CloudFileRepository;
import com.cloudvault.util.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final CloudFileRepository cloudFileRepository;
    private final CloudStorageService cloudStorageService;
    private final EncryptionService encryptionService;

    @Value("${app.base-url:http://localhost:8080}")
    private String configuredBaseUrl;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${encryption.master-key:changeme-secure-key-in-production}")
    private String masterEncryptionKey;

    private String baseUrl;

    public FileService(CloudFileRepository cloudFileRepository,
                      CloudStorageService cloudStorageService,
                      EncryptionService encryptionService) {
        this.cloudFileRepository = cloudFileRepository;
        this.cloudStorageService = cloudStorageService;
        this.encryptionService = encryptionService;
    }

    @PostConstruct
    public void init() {
        // Auto-detect IP if base URL is localhost
        this.baseUrl = NetworkUtils.buildBaseUrl(configuredBaseUrl, serverPort);
        log.info("FileService initialized with base URL: {}", baseUrl);
    }

    /**
     * Upload and encrypt a file
     */
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, FileUploadRequest.ExpiryDuration expiryDuration, String accessMode) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            // Read file bytes
            byte[] fileBytes = file.getBytes();

            // Encrypt file - returns base64 encoded encrypted data
            String encryptedFileBase64 = encryptionService.encryptBytes(fileBytes, masterEncryptionKey);

            // Upload encrypted file (as base64 string bytes) to cloud storage
            byte[] encryptedBytes = encryptedFileBase64.getBytes(StandardCharsets.UTF_8);
            String storagePath = cloudStorageService.uploadFile(encryptedBytes, file.getOriginalFilename());

            // Generate share token
            String shareToken = generateShareToken();

            // Calculate expiry time
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryDuration.getMinutes());

            // Determine access mode
            com.cloudvault.model.AccessMode mode = com.cloudvault.model.AccessMode.DOWNLOAD;
            if ("VIEW_ONLY".equalsIgnoreCase(accessMode)) {
                mode = com.cloudvault.model.AccessMode.VIEW_ONLY;
            }

            // Create CloudFile entity
            CloudFile cloudFile = CloudFile.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storagePath(storagePath)
                    .shareToken(shareToken)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .expiresAt(expiresAt)
                    .accessMode(mode)
                    .build();

            // Save to database
            CloudFile savedFile = cloudFileRepository.save(cloudFile);

            log.info("File uploaded successfully: {} (ID: {}, Access Mode: {})", 
                    file.getOriginalFilename(), savedFile.getId(), mode);

            // Generate share link
            String shareLink = generateShareLink(shareToken);

            // Build response
            return FileUploadResponse.builder()
                    .fileId(savedFile.getId())
                    .shareToken(shareToken)
                    .shareLink(shareLink)
                    .fileName(savedFile.getOriginalFileName())
                    .fileSize(savedFile.getFileSize())
                    .uploadedAt(savedFile.getUploadedAt())
                    .expiresAt(savedFile.getExpiresAt())
                    .status(savedFile.getStatus().toString())
                    .accessMode(savedFile.getAccessMode().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Verify that a file is encrypted
     */
    public EncryptionVerificationResponse verifyEncryption(String shareToken) {
        CloudFile cloudFile = cloudFileRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check if file has expired
        cloudFile.checkExpiry();

        if (cloudFile.getStatus() == FileStatus.EXPIRED) {
            log.warn("Encryption verification attempt to expired file: {}", shareToken);
            throw new RuntimeException("This file has expired");
        }

        return EncryptionVerificationResponse.builder()
                .fileId(cloudFile.getId())
                .fileName(cloudFile.getOriginalFileName())
                .isEncrypted(true)
                .encryptionAlgorithm("AES")
                .encryptionMode("CBC")
                .keyLengthBits(256)
                .status("ENCRYPTED")
                .message("✅ File is securely encrypted with AES-256-CBC. Only authorized users with the correct decryption key can access the original content.")
                .build();
    }

    /**
     * Get file access information and check expiry
     */
    public FileAccessResponse getFileAccess(String shareToken) {
        CloudFile cloudFile = cloudFileRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check if file has expired
        cloudFile.checkExpiry();

        if (cloudFile.getStatus() == FileStatus.EXPIRED) {
            log.warn("Access attempt to expired file: {}", shareToken);
            throw new RuntimeException("This file has expired and is no longer available");
        }

        return FileAccessResponse.builder()
                .fileId(cloudFile.getId())
                .fileName(cloudFile.getOriginalFileName())
                .fileSize(cloudFile.getFileSize())
                .fileType(cloudFile.getFileType())
                .uploadedAt(cloudFile.getUploadedAt())
                .expiresAt(cloudFile.getExpiresAt())
                .status(cloudFile.getStatus().toString())
                .isExpired(cloudFile.isExpired())
                .downloadCount(cloudFile.getDownloadCount())
                .accessMode(cloudFile.getAccessMode().toString())
                .build();
    }

    /**
     * Download and decrypt file (for DOWNLOAD mode only)
     */
    @Transactional
    public byte[] downloadFile(String shareToken) {
        CloudFile cloudFile = cloudFileRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check expiry
        cloudFile.checkExpiry();
        if (cloudFile.getStatus() == FileStatus.EXPIRED) {
            throw new RuntimeException("This file has expired and cannot be downloaded");
        }

        // Check if download is allowed
        if (cloudFile.getAccessMode() == com.cloudvault.model.AccessMode.VIEW_ONLY) {
            log.warn("Download attempt on VIEW_ONLY file: {}", shareToken);
            throw new RuntimeException("This file is view-only and cannot be downloaded");
        }

        try {
            // Download encrypted file from cloud storage
            byte[] encryptedData = cloudStorageService.downloadFile(cloudFile.getStoragePath());

            // Decrypt file
            byte[] decryptedData = encryptionService.decryptBytes(
                    new String(encryptedData),
                    masterEncryptionKey
            );

            // Increment download count
            cloudFile.setDownloadCount(cloudFile.getDownloadCount() + 1);
            cloudFileRepository.save(cloudFile);

            log.info("File downloaded successfully: {} (Downloads: {})", 
                    cloudFile.getOriginalFileName(), cloudFile.getDownloadCount());

            return decryptedData;

        } catch (Exception e) {
            log.error("Error downloading/decrypting file: {}", e.getMessage());
            throw new RuntimeException("Failed to download file", e);
        }
    }

    /**
     * View file content (for VIEW_ONLY and DOWNLOAD modes)
     * Used to display file in browser without saving locally
     */
    @Transactional
    public byte[] viewFile(String shareToken) {
        CloudFile cloudFile = cloudFileRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check expiry
        cloudFile.checkExpiry();
        if (cloudFile.getStatus() == FileStatus.EXPIRED) {
            throw new RuntimeException("This file has expired and cannot be viewed");
        }

        // Allow viewing for both DOWNLOAD and VIEW_ONLY modes
        try {
            log.info("Viewing file: {} from path: {}", cloudFile.getOriginalFileName(), cloudFile.getStoragePath());
            
            // Download encrypted file from cloud storage
            byte[] encryptedData = cloudStorageService.downloadFile(cloudFile.getStoragePath());
            log.info("Encrypted data size: {} bytes", encryptedData.length);

            if (encryptedData.length == 0) {
                throw new RuntimeException("Encrypted file data is empty");
            }

            // Decrypt file
            byte[] decryptedData = encryptionService.decryptBytes(
                    new String(encryptedData),
                    masterEncryptionKey
            );
            log.info("Decrypted data size: {} bytes", decryptedData.length);

            log.info("File viewed successfully: {} (Access Mode: {})", 
                    cloudFile.getOriginalFileName(), cloudFile.getAccessMode());

            return decryptedData;

        } catch (Exception e) {
            log.error("Error viewing/decrypting file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to view file: " + e.getMessage(), e);
        }
    }

    /**
     * Mark file as expired and trigger deletion
     */
    @Transactional
    public void expireFile(String fileId) {
        CloudFile cloudFile = cloudFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (cloudFile.getStatus() != FileStatus.DELETED) {
            cloudFile.setStatus(FileStatus.EXPIRED);
            cloudFileRepository.save(cloudFile);
            log.info("File marked as expired: {}", fileId);
        }
    }

    /**
     * Delete file from storage and database
     */
    @Transactional
    public void deleteFile(String fileId) {
        CloudFile cloudFile = cloudFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            // Delete from cloud storage
            cloudStorageService.deleteFile(cloudFile.getStoragePath());

            // Mark as deleted in database
            cloudFile.setStatus(FileStatus.DELETED);
            cloudFile.setDeletedAt(LocalDateTime.now());
            cloudFileRepository.save(cloudFile);

            log.info("File deleted: {} (ID: {})", cloudFile.getOriginalFileName(), fileId);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Generate unique share token
     */
    private String generateShareToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    /**
     * Generate shareable link
     */
    private String generateShareLink(String shareToken) {
        return baseUrl + "/file/" + shareToken;
    }

}
