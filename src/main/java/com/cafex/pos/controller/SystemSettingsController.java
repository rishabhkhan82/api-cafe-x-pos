package com.cafex.pos.controller;

import com.cafex.pos.entity.SystemSetting;
import com.cafex.pos.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/settings")
@CrossOrigin(origins = "*")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    /**
     * Get all public system settings (accessible before login)
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicSettings() {
        try {
            List<SystemSetting> settings = systemSettingsService.getPublicSettings();
            return ResponseEntity.ok(Map.of(
                "settings", settings,
                "version", "1.0",
                "lastModified", java.time.Instant.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all system settings (admin only)
     */
    @GetMapping
    public ResponseEntity<List<SystemSetting>> getAllSettings() {
        try {
            List<SystemSetting> settings = systemSettingsService.getAllSettings();
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a system setting
     */
    @PutMapping("/{settingId}")
    public ResponseEntity<SystemSetting> updateSetting(
            @PathVariable String settingId,
            @RequestBody Map<String, Object> request) {
        try {
            Object value = request.get("value");
            SystemSetting updatedSetting = systemSettingsService.updateSetting(settingId, value);
            return ResponseEntity.ok(updatedSetting);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Reset setting to default value
     */
    @PostMapping("/{settingId}/reset")
    public ResponseEntity<SystemSetting> resetSetting(@PathVariable String settingId) {
        try {
            SystemSetting resetSetting = systemSettingsService.resetSetting(settingId);
            return ResponseEntity.ok(resetSetting);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Export all settings
     */
    @GetMapping("/export")
    public ResponseEntity<List<Map<String, Object>>> exportSettings() {
        try {
            List<Map<String, Object>> exportData = systemSettingsService.exportSettings();
            return ResponseEntity.ok(exportData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Import settings from backup
     */
    @PostMapping("/import")
    public ResponseEntity<String> importSettings(@RequestBody List<Map<String, Object>> settingsData) {
        try {
            systemSettingsService.importSettings(settingsData);
            return ResponseEntity.ok("Settings imported successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}