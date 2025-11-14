package com.cafex.pos.service;

import com.cafex.pos.entity.SystemSetting;
import com.cafex.pos.repository.SystemSettingsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SystemSettingsService {

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Get system settings (single record)
     */
    public SystemSetting getSystemSettings() {
        Optional<SystemSetting> existing = systemSettingsRepository.findById(1L);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            SystemSetting settings = new SystemSetting();
            settings.setId(1L);
            settings.setCreatedAt(LocalDateTime.now());
            settings.setUpdatedAt(LocalDateTime.now());
            // Set default values for required fields
            settings.setMaintenanceMode(Boolean.FALSE);
            settings.setBackupEnabled(Boolean.TRUE);
            settings.setCacheEnabled(Boolean.TRUE);
            settings.setTwoFactorRequired(Boolean.FALSE);
            settings.setEmailNotifications(Boolean.TRUE);
            settings.setSmsNotifications(Boolean.FALSE);
            settings.setNotificationBatchSize(100);
            settings.setWebhookRetries(3);
            return systemSettingsRepository.save(settings);
        }
    }

    /**
     * Save system settings
     */
    public SystemSetting saveSystemSettings(SystemSetting settings) {
        settings.setId(1L);
        settings.setUpdatedAt(LocalDateTime.now());
        return systemSettingsRepository.save(settings);
    }


}