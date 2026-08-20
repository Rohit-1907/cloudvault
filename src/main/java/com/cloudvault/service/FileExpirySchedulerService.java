package com.cloudvault.service;

import com.cloudvault.model.CloudFile;
import com.cloudvault.repository.CloudFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FileExpirySchedulerService {

    private final CloudFileRepository cloudFileRepository;
    private final FileService fileService;

    public FileExpirySchedulerService(CloudFileRepository cloudFileRepository, FileService fileService) {
        this.cloudFileRepository = cloudFileRepository;
        this.fileService = fileService;
    }

    /**
     * Scheduled task to check and delete expired files
     * Runs every 5 minutes
     */
    @Scheduled(fixedDelayString = "${scheduler.file-expiry.interval:300000}")
    @Transactional
    public void cleanupExpiredFiles() {
        try {
            log.info("Starting expired file cleanup task...");

            // Find all files that have expired
            List<CloudFile> expiredFiles = cloudFileRepository.findExpiredActiveFiles(LocalDateTime.now());

            if (expiredFiles.isEmpty()) {
                log.debug("No expired files found");
                return;
            }

            log.info("Found {} expired files to clean up", expiredFiles.size());

            // Delete each expired file
            for (CloudFile file : expiredFiles) {
                try {
                    fileService.deleteFile(file.getId());
                    log.info("Successfully deleted expired file: {}", file.getId());
                } catch (Exception e) {
                    log.error("Failed to delete expired file {}: {}", file.getId(), e.getMessage());
                }
            }

            log.info("Expired file cleanup task completed. Deleted {} files", expiredFiles.size());

        } catch (Exception e) {
            log.error("Error during expired file cleanup: {}", e.getMessage());
        }
    }

    /**
     * Optional: Manual trigger to cleanup expired files
     */
    public void triggerCleanupNow() {
        cleanupExpiredFiles();
    }

}
