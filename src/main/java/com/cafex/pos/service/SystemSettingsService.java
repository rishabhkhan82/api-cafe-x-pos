package com.cafex.pos.service;

import com.cafex.pos.entity.SystemSetting;
import com.cafex.pos.repository.SystemSettingsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SystemSettingsService {

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get all public settings (accessible before login)
     */
    public List<SystemSetting> getPublicSettings() {
        return systemSettingsRepository.findByIsPublicTrue();
    }

    /**
     * Get all settings (admin only)
     */
    public List<SystemSetting> getAllSettings() {
        return systemSettingsRepository.findAll();
    }

    /**
     * Update a setting value
     */
    public SystemSetting updateSetting(String settingId, Object value) {
        SystemSetting setting = systemSettingsRepository.findById(settingId)
            .orElseThrow(() -> new RuntimeException("Setting not found: " + settingId));

        try {
            setting.setValue(objectMapper.writeValueAsString(value));
            return systemSettingsRepository.save(setting);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update setting value", e);
        }
    }

    /**
     * Reset setting to default value
     */
    public SystemSetting resetSetting(String settingId) {
        SystemSetting setting = systemSettingsRepository.findById(settingId)
            .orElseThrow(() -> new RuntimeException("Setting not found: " + settingId));

        setting.setValue(setting.getDefaultValue());
        return systemSettingsRepository.save(setting);
    }

    /**
     * Export settings for backup
     */
    public List<Map<String, Object>> exportSettings() {
        return systemSettingsRepository.findAll().stream()
            .map(setting -> Map.of(
                "id", setting.getId(),
                "name", setting.getName(),
                "category", setting.getCategory(),
                "value", parseJsonValue(setting.getValue()),
                "defaultValue", parseJsonValue(setting.getDefaultValue()),
                "lastModified", setting.getUpdatedAt()
            ))
            .collect(Collectors.toList());
    }

    /**
     * Import settings from backup
     */
    public void importSettings(List<Map<String, Object>> settingsData) {
        for (Map<String, Object> data : settingsData) {
            String settingId = (String) data.get("id");
            Object value = data.get("value");

            if (settingId != null && value != null) {
                try {
                    SystemSetting setting = systemSettingsRepository.findById(settingId)
                        .orElseThrow(() -> new RuntimeException("Setting not found: " + settingId));

                    setting.setValue(objectMapper.writeValueAsString(value));
                    systemSettingsRepository.save(setting);
                } catch (Exception e) {
                    // Log error but continue with other settings
                    System.err.println("Failed to import setting " + settingId + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Initialize default settings if not exists
     */
    public void initializeDefaultSettings() {
        if (systemSettingsRepository.count() == 0) {
            createDefaultSettings();
        }
    }

    private void createDefaultSettings() {
        // General Settings
        createSetting("platform_name", "Platform Name", "The name displayed across the platform",
                     "general", "text", "CafeX POS", "CafeX POS", null, null, false, true);

        createSetting("platform_url", "Platform URL", "Base URL for the platform",
                     "general", "text", "https://cafex.com", "https://cafex.com", null,
                     Map.of("required", true, "pattern", "^https?://.+"), false, true);

        createSetting("currency", "Default Currency", "Default currency for transactions",
                     "general", "select", "INR", "INR", List.of("INR", "USD", "EUR", "GBP"), null, false, true);

        createSetting("platform_logo", "Platform Logo URL", "URL for the platform logo",
                     "general", "text", "", "", null, Map.of("pattern", "^https?://.+"), false, true);

        createSetting("default_language", "Default Language", "Default language for the platform",
                     "general", "select", "en-IN", "en-IN", List.of("en-IN", "hi-IN", "mr-IN"), null, false, true);

        createSetting("maintenance_message", "Maintenance Message", "Message displayed during maintenance",
                     "general", "textarea", "", "", null, null, false, true);

        createSetting("file_upload_max_size", "Max File Upload Size (MB)", "Maximum file size for uploads",
                     "general", "number", 10, 10, null, Map.of("min", 1, "max", 100), false, true);

        createSetting("backup_enabled", "Enable Backups", "Automatically backup system data",
                     "general", "boolean", true, true, null, null, false, true);

        createSetting("backup_frequency", "Backup Frequency", "How often to perform backups",
                     "general", "select", "daily", "daily", List.of("daily", "weekly", "monthly"), null, false, true);

        createSetting("support_email", "Support Email", "Email address for support",
                      "general", "text", "", "", null, Map.of("pattern", "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"), false, true);

        createSetting("support_phone", "Support Phone", "Phone number for support",
                     "general", "text", "", "", null, Map.of("pattern", "^[+\\d\\s\\-\\(\\)]+$"), false, true);

        createSetting("terms_url", "Terms of Service URL", "URL for terms of service",
                     "general", "text", "", "", null, Map.of("pattern", "^https?://.+"), false, true);

        createSetting("privacy_url", "Privacy Policy URL", "URL for privacy policy",
                     "general", "text", "", "", null, Map.of("pattern", "^https?://.+"), false, true);

        // Performance Settings
        createSetting("max_concurrent_users", "Max Concurrent Users", "Maximum number of users that can be active simultaneously",
                     "performance", "number", 1000, 1000, null, Map.of("required", true, "min", 10, "max", 10000), true, true);

        createSetting("cache_enabled", "Enable Caching", "Enable system-wide caching for better performance",
                     "performance", "boolean", true, true, null, null, true, true);

        createSetting("cache_ttl", "Cache TTL (seconds)", "Time-to-live for cached data",
                     "performance", "number", 3600, 3600, null, Map.of("required", true, "min", 60, "max", 86400), false, true);

        // Security Settings
        createSetting("session_timeout", "Session Timeout (minutes)", "Automatic logout after inactivity",
                     "security", "number", 30, 30, null, Map.of("required", true, "min", 5, "max", 480), false, true);

        createSetting("password_min_length", "Minimum Password Length", "Minimum characters required for passwords",
                     "security", "number", 8, 8, null, Map.of("required", true, "min", 6, "max", 128), false, true);

        createSetting("two_factor_required", "Require 2FA", "Require two-factor authentication for all users",
                     "security", "boolean", false, false, null, null, false, true);

        // Notification Settings
        createSetting("email_notifications", "Email Notifications", "Enable email notifications system-wide",
                     "notifications", "boolean", true, true, null, null, false, true);

        createSetting("sms_notifications", "SMS Notifications", "Enable SMS notifications for critical alerts",
                     "notifications", "boolean", false, false, null, null, false, true);

        createSetting("notification_batch_size", "Notification Batch Size", "Maximum notifications sent per batch",
                     "notifications", "number", 100, 100, null, Map.of("required", true, "min", 10, "max", 1000), false, true);

        // Integration Settings
        createSetting("api_rate_limit", "API Rate Limit", "Maximum API requests per minute",
                     "integrations", "number", 1000, 1000, null, Map.of("required", true, "min", 10, "max", 10000), false, true);

        createSetting("webhook_retries", "Webhook Retry Attempts", "Number of retry attempts for failed webhooks",
                     "integrations", "number", 3, 3, null, Map.of("required", true, "min", 0, "max", 10), false, true);

        createSetting("maintenance_mode", "Maintenance Mode", "Put the platform in maintenance mode",
                     "general", "boolean", false, false, null, null, true, true);
    }

    private void createSetting(String id, String name, String description, String category,
                              String type, Object value, Object defaultValue, List<String> options,
                              Map<String, Object> validation, boolean requiresRestart, boolean isPublic) {
        try {
            SystemSetting setting = new SystemSetting();
            setting.setId(id);
            setting.setName(name);
            setting.setDescription(description);
            setting.setCategory(category);
            setting.setType(type);
            setting.setValue(objectMapper.writeValueAsString(value));
            setting.setDefaultValue(objectMapper.writeValueAsString(defaultValue));

            if (options != null) {
                setting.setOptions(objectMapper.writeValueAsString(options));
            }

            if (validation != null) {
                setting.setValidation(objectMapper.writeValueAsString(validation));
            }

            setting.setRequiresRestart(requiresRestart);
            setting.setIsPublic(isPublic);

            systemSettingsRepository.save(setting);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create setting: " + id, e);
        }
    }

    private Object parseJsonValue(String jsonValue) {
        if (jsonValue == null) return null;
        try {
            return objectMapper.readValue(jsonValue, Object.class);
        } catch (Exception e) {
            return jsonValue; // Return as string if parsing fails
        }
    }
}