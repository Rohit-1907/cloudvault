package com.cloudvault.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cloud_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String shareToken;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessMode accessMode;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private Integer downloadCount;

    @Transient
    private boolean isExpired;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
        this.downloadCount = 0;
        this.status = FileStatus.ACTIVE;
        if (this.accessMode == null) {
            this.accessMode = AccessMode.DOWNLOAD; // Default to DOWNLOAD
        }
    }

    public void checkExpiry() {
        if (LocalDateTime.now().isAfter(expiresAt)) {
            this.status = FileStatus.EXPIRED;
            this.isExpired = true;
        }
    }

}
