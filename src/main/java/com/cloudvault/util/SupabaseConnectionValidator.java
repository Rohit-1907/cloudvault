package com.cloudvault.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Validator to check Supabase connection and configuration on application startup
 */
@Slf4j
@Component
public class SupabaseConnectionValidator {

    @Autowired
    private DataSource dataSource;

    /**
     * Runs on application startup to validate Supabase connection
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateConnection() {
        log.info("========================================");
        log.info("CloudVault Supabase Connection Validator");
        log.info("========================================");

        try {
            // Test database connection
            testDatabaseConnection();
            log.info("✅ Database connection successful");

            // Check cloud_files table
            checkCloudFilesTable();
            log.info("✅ cloud_files table exists");

            // Test query
            testTableQuery();
            log.info("✅ Database queries working");

            log.info("========================================");
            log.info("✅ All Supabase checks passed!");
            log.info("========================================");

        } catch (Exception e) {
            log.error("❌ Supabase validation failed: {}", e.getMessage());
            log.error("Connection Details Check:");
            log.error("  - Verify SUPABASE_HOST is correct");
            log.error("  - Verify database password");
            log.error("  - Verify port 5432 is accessible");
            log.error("  - Verify SSL is enabled (sslmode=require)");
            log.warn("Application will continue, but cloud features may not work");
        }
    }

    /**
     * Test basic database connection
     */
    private void testDatabaseConnection() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                log.info("Database connection valid");
                
                // Get database info
                String dbName = conn.getCatalog();
                String dbVersion = conn.getMetaData().getDatabaseProductVersion();
                log.info("Connected to: {} (PostgreSQL {})", dbName, dbVersion);
            } else {
                throw new Exception("Database connection validation failed");
            }
        }
    }

    /**
     * Check if cloud_files table exists
     */
    private void checkCloudFilesTable() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'cloud_files'";
            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next() && rs.getInt(1) > 0) {
                    log.info("cloud_files table found");
                } else {
                    throw new Exception("cloud_files table not found. Run init.sql to create tables");
                }
            }
        }
    }

    /**
     * Test table operations
     */
    private void testTableQuery() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Count files
            String countQuery = "SELECT COUNT(*) as count FROM cloud_files";
            try (ResultSet rs = stmt.executeQuery(countQuery)) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    log.info("Total files in database: {}", count);
                }
            }

            // Get database storage info
            String sizeQuery = "SELECT pg_size_pretty(pg_total_relation_size('cloud_files')) as size";
            try (ResultSet rs = stmt.executeQuery(sizeQuery)) {
                if (rs.next()) {
                    log.info("cloud_files table size: {}", rs.getString("size"));
                }
            }
        }
    }

    /**
     * Method to manually check connection (can be called via endpoint)
     */
    public boolean validateSupabaseConnection() {
        try {
            testDatabaseConnection();
            checkCloudFilesTable();
            return true;
        } catch (Exception e) {
            log.error("Validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get database stats
     */
    public String getDatabaseStats() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            StringBuilder stats = new StringBuilder();

            // Active files count
            String activeQuery = "SELECT COUNT(*) FROM cloud_files WHERE status = 'ACTIVE'";
            try (ResultSet rs = stmt.executeQuery(activeQuery)) {
                if (rs.next()) {
                    stats.append("Active files: ").append(rs.getInt(1)).append("\n");
                }
            }

            // Expired files count
            String expiredQuery = "SELECT COUNT(*) FROM cloud_files WHERE status = 'EXPIRED'";
            try (ResultSet rs = stmt.executeQuery(expiredQuery)) {
                if (rs.next()) {
                    stats.append("Expired files: ").append(rs.getInt(1)).append("\n");
                }
            }

            // Total downloads
            String downloadQuery = "SELECT SUM(download_count) FROM cloud_files";
            try (ResultSet rs = stmt.executeQuery(downloadQuery)) {
                if (rs.next()) {
                    long total = rs.getLong(1);
                    stats.append("Total downloads: ").append(total == 0 ? "0" : total).append("\n");
                }
            }

            // Table size
            String sizeQuery = "SELECT pg_size_pretty(pg_total_relation_size('cloud_files')) as size";
            try (ResultSet rs = stmt.executeQuery(sizeQuery)) {
                if (rs.next()) {
                    stats.append("Table size: ").append(rs.getString("size")).append("\n");
                }
            }

            return stats.toString();

        } catch (Exception e) {
            log.error("Error getting stats: {}", e.getMessage());
            return "Error retrieving statistics";
        }
    }

}
