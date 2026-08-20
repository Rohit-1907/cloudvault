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
public class FileAccessResponse {
    private String fileId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private LocalDateTime uploadedAt;
    private LocalDateTime expiresAt;
    private String status;
    private Boolean isExpired;
    private Integer downloadCount;
    private String accessMode;
}
