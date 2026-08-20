package com.cloudvault.controller;

import com.cloudvault.dto.FileAccessResponse;
import com.cloudvault.dto.FileUploadRequest;
import com.cloudvault.dto.FileUploadResponse;
import com.cloudvault.dto.EncryptionVerificationResponse;
import com.cloudvault.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Upload a file with expiry duration and access mode
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("expiry") FileUploadRequest.ExpiryDuration expiry,
            @RequestParam(value = "accessMode", defaultValue = "DOWNLOAD") String accessMode) {

        try {
            log.info("File upload request: {} (Expiry: {}, AccessMode: {})", 
                    file.getOriginalFilename(), expiry, accessMode);

            FileUploadResponse response = fileService.uploadFile(file, expiry, accessMode);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get file access information (check if file is available)
     * GET /api/files/access/{shareToken}
     */
    @GetMapping("/access/{shareToken}")
    public ResponseEntity<FileAccessResponse> getFileAccess(@PathVariable String shareToken) {
        try {
            log.info("File access request: {}", shareToken);

            FileAccessResponse response = fileService.getFileAccess(shareToken);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("File access denied: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error accessing file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download file with given share token
     * GET /api/files/download/{shareToken}
     */
    @GetMapping("/download/{shareToken}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String shareToken) {
        try {
            log.info("File download request: {}", shareToken);

            // Get file info
            FileAccessResponse fileInfo = fileService.getFileAccess(shareToken);

            // Download and decrypt file
            byte[] fileData = fileService.downloadFile(shareToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileInfo.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.length))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileData);

        } catch (RuntimeException e) {
            log.warn("File download denied: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * View file content in browser (for VIEW_ONLY and DOWNLOAD modes)
     * GET /api/files/view/{shareToken}
     */
    @GetMapping("/view/{shareToken}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String shareToken) {
        try {
            log.info("File view request: {}", shareToken);

            // Get file info
            FileAccessResponse fileInfo = fileService.getFileAccess(shareToken);

            // View and decrypt file
            byte[] fileData = fileService.viewFile(shareToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + fileInfo.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.length))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileData);

        } catch (RuntimeException e) {
            log.warn("File view denied: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error viewing file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verify that file is encrypted
     * GET /api/files/verify/{shareToken}
     */
    @GetMapping("/verify/{shareToken}")
    public ResponseEntity<EncryptionVerificationResponse> verifyEncryption(@PathVariable String shareToken) {
        try {
            log.info("Encryption verification request: {}", shareToken);

            EncryptionVerificationResponse response = fileService.verifyEncryption(shareToken);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("Encryption verification denied: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error verifying encryption: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check endpoint
     * GET /api/files/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CloudVault API is running");
    }

}
