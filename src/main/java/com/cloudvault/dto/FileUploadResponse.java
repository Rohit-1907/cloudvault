package com.cloudvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {
    private String fileId;
    private String shareToken;
    private String shareLink;
    private String fileName;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private LocalDateTime expiresAt;
    private String status;
    private String accessMode;
}
