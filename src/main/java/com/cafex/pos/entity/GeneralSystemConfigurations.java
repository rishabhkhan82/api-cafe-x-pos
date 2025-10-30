package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "general_system_configurations")
public class GeneralSystemConfigurations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_name")
    private String platformName;

    @Column(name = "platform_logo")
    private String platformLogo;

    @Column(name = "default_language")
    private String defaultLanguage;

    @Column(name = "default_timezone")
    private String defaultTimezone;

    @Column(name = "default_currency")
    private String defaultCurrency;

    @Column(name = "maintenance_mode", nullable = false)
    private Boolean maintenanceMode = false;

    @Column(name = "maintenance_message", columnDefinition = "TEXT")
    private String maintenanceMessage;

    @Column(name = "api_rate_limit", nullable = false)
    private Integer apiRateLimit = 1000;

    @Column(name = "file_upload_max_size", nullable = false)
    private Integer fileUploadMaxSize = 10;

    @Column(name = "session_timeout", nullable = false)
    private Integer sessionTimeout = 30;

    @Column(name = "cache_enabled", nullable = false)
    private Boolean cacheEnabled = true;

    @Column(name = "cache_duration", nullable = false)
    private Integer cacheDuration = 60;

    @Column(name = "backup_enabled", nullable = false)
    private Boolean backupEnabled = true;

    @Column(name = "backup_frequency")
    private String backupFrequency;

    @Column(name = "support_email")
    private String supportEmail;

    @Column(name = "support_phone")
    private String supportPhone;

    @Column(name = "terms_url")
    private String termsUrl;

    @Column(name = "privacy_url")
    private String privacyUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public GeneralSystemConfigurations() {}

    public GeneralSystemConfigurations(String platformName, String defaultLanguage, String defaultTimezone, String defaultCurrency) {
        this.platformName = platformName;
        this.defaultLanguage = defaultLanguage;
        this.defaultTimezone = defaultTimezone;
        this.defaultCurrency = defaultCurrency;
        this.maintenanceMode = false;
        this.apiRateLimit = 1000;
        this.fileUploadMaxSize = 10;
        this.sessionTimeout = 30;
        this.cacheEnabled = true;
        this.cacheDuration = 60;
        this.backupEnabled = true;
        this.backupFrequency = "daily";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPlatformLogo() {
        return platformLogo;
    }

    public void setPlatformLogo(String platformLogo) {
        this.platformLogo = platformLogo;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDefaultTimezone() {
        return defaultTimezone;
    }

    public void setDefaultTimezone(String defaultTimezone) {
        this.defaultTimezone = defaultTimezone;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(Boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        this.updatedAt = LocalDateTime.now();
    }

    public String getMaintenanceMessage() {
        return maintenanceMessage;
    }

    public void setMaintenanceMessage(String maintenanceMessage) {
        this.maintenanceMessage = maintenanceMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getApiRateLimit() {
        return apiRateLimit;
    }

    public void setApiRateLimit(Integer apiRateLimit) {
        this.apiRateLimit = apiRateLimit;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getFileUploadMaxSize() {
        return fileUploadMaxSize;
    }

    public void setFileUploadMaxSize(Integer fileUploadMaxSize) {
        this.fileUploadMaxSize = fileUploadMaxSize;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(Boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getCacheDuration() {
        return cacheDuration;
    }

    public void setCacheDuration(Integer cacheDuration) {
        this.cacheDuration = cacheDuration;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getBackupEnabled() {
        return backupEnabled;
    }

    public void setBackupEnabled(Boolean backupEnabled) {
        this.backupEnabled = backupEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBackupFrequency() {
        return backupFrequency;
    }

    public void setBackupFrequency(String backupFrequency) {
        this.backupFrequency = backupFrequency;
        this.updatedAt = LocalDateTime.now();
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
        this.updatedAt = LocalDateTime.now();
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTermsUrl() {
        return termsUrl;
    }

    public void setTermsUrl(String termsUrl) {
        this.termsUrl = termsUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPrivacyUrl() {
        return privacyUrl;
    }

    public void setPrivacyUrl(String privacyUrl) {
        this.privacyUrl = privacyUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isInMaintenance() {
        return maintenanceMode != null && maintenanceMode;
    }

    public void enableMaintenance(String message) {
        this.maintenanceMode = true;
        this.maintenanceMessage = message;
        this.updatedAt = LocalDateTime.now();
    }

    public void disableMaintenance() {
        this.maintenanceMode = false;
        this.maintenanceMessage = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCacheEnabled() {
        return cacheEnabled != null && cacheEnabled;
    }

    public boolean isBackupEnabled() {
        return backupEnabled != null && backupEnabled;
    }

    public boolean isDailyBackup() {
        return "daily".equals(backupFrequency);
    }

    public boolean isWeeklyBackup() {
        return "weekly".equals(backupFrequency);
    }

    public boolean isMonthlyBackup() {
        return "monthly".equals(backupFrequency);
    }

    public boolean hasSupportContact() {
        return (supportEmail != null && !supportEmail.trim().isEmpty()) ||
               (supportPhone != null && !supportPhone.trim().isEmpty());
    }

    public boolean hasLegalDocuments() {
        return (termsUrl != null && !termsUrl.trim().isEmpty()) ||
               (privacyUrl != null && !privacyUrl.trim().isEmpty());
    }

    public void updateConfiguration(String key, Object value, User updatedBy) {
        // This would be handled by service layer to update specific fields
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDefaultMaintenanceMessage() {
        if (maintenanceMessage != null && !maintenanceMessage.trim().isEmpty()) {
            return maintenanceMessage;
        }
        return "System is currently under maintenance. Please try again later.";
    }
}
