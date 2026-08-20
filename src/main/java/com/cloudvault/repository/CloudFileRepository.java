package com.cloudvault.repository;

import com.cloudvault.model.CloudFile;
import com.cloudvault.model.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CloudFileRepository extends JpaRepository<CloudFile, String> {

    Optional<CloudFile> findByShareToken(String shareToken);

    List<CloudFile> findByStatus(FileStatus status);

    @Query("SELECT cf FROM CloudFile cf WHERE cf.expiresAt <= :now AND cf.status = 'ACTIVE'")
    List<CloudFile> findExpiredActiveFiles(@Param("now") LocalDateTime now);

    List<CloudFile> findByStatusAndDeletedAtIsNull(FileStatus status);

}
