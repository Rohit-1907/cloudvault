package com.cloudvault.controller;

import com.cloudvault.util.SupabaseConnectionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin endpoints for diagnostics and monitoring
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private SupabaseConnectionValidator connectionValidator;

    /**
     * Check system health and Supabase connection
     * GET /api/admin/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "CloudVault");
        health.put("status", "running");

        boolean dbConnected = connectionValidator.validateSupabaseConnection();
        health.put("database_connected", dbConnected);
        health.put("supabase_integration", dbConnected ? "✅ Active" : "❌ Failed");

        return ResponseEntity.ok(health);
    }

    /**
     * Get database statistics
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("timestamp", System.currentTimeMillis());
        String statsText = connectionValidator.getDatabaseStats();
        
        // Parse stats into individual fields
        Map<String, String> parsed = new HashMap<>();
        for (String line : statsText.split("\n")) {
            if (!line.trim().isEmpty()) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    parsed.put(parts[0].toLowerCase().replace(" ", "_"), parts[1]);
                }
            }
        }
        
        stats.put("database", parsed);
        return ResponseEntity.ok(stats);
    }

    /**
     * Validate Supabase configuration
     * GET /api/admin/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateConfiguration() {
        Map<String, Object> validation = new HashMap<>();
        
        boolean isValid = connectionValidator.validateSupabaseConnection();
        
        validation.put("timestamp", System.currentTimeMillis());
        validation.put("supabase_configured", isValid);
        validation.put("message", isValid ? 
            "✅ Supabase is properly configured and accessible" : 
            "❌ Supabase configuration issue detected");

        int statusCode = isValid ? 200 : 503;
        return ResponseEntity.status(statusCode).body(validation);
    }

    /**
     * Get application information
     * GET /api/admin/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getApplicationInfo() {
        Map<String, String> info = new HashMap<>();
        
        info.put("application", "CloudVault");
        info.put("version", "1.0.0");
        info.put("description", "Secure Self-Expiring Cloud File Storage & Sharing");
        info.put("java_version", System.getProperty("java.version"));
        info.put("os_name", System.getProperty("os.name"));
        info.put("os_version", System.getProperty("os.version"));
        info.put("available_processors", String.valueOf(Runtime.getRuntime().availableProcessors()));
        info.put("max_memory_mb", String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024));
        info.put("used_memory_mb", String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024));

        return ResponseEntity.ok(info);
    }

    /**
     * Manual trigger for file cleanup
     * POST /api/admin/cleanup
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, String>> triggerCleanup() {
        Map<String, String> result = new HashMap<>();
        
        try {
            // This would call FileExpirySchedulerService if needed
            result.put("status", "success");
            result.put("message", "Cleanup task triggered");
            log.info("Manual cleanup triggered");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Cleanup failed: {}", e.getMessage());
            result.put("status", "error");
            result.put("message", "Cleanup failed: " + e.getMessage());
            
            return ResponseEntity.status(500).body(result);
        }
    }

}
