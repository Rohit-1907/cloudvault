package com.cloudvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptionVerificationResponse {
    private String fileId;
    private String fileName;
    private Boolean isEncrypted;
    private String encryptionAlgorithm;
    private String encryptionMode;
    private Integer keyLengthBits;
    private String status;
    private String message;
}
