package com.cafex.pos.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "platform_name")
    private String platformName;

    @Column(name = "platform_url")
    private String platformUrl;

    @Column(name = "platform_logo")
    private String platformLogo;

    @Column(name = "default_language")
    private String defaultLanguage;

    @Column(name = "maintenance_mode")
    private Boolean maintenanceMode;

    @Column(name = "maintenance_message")
    private String maintenanceMessage;

    @Column(name = "file_upload_max_size")
    private Integer fileUploadMaxSize;

    @Column(name = "backup_enabled")
    private Boolean backupEnabled;

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

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "currency")
    private String currency;

    @Column(name = "max_concurrent_users")
    private Integer maxConcurrentUsers;

    @Column(name = "cache_enabled")
    private Boolean cacheEnabled;

    @Column(name = "cache_ttl")
    private Integer cacheTtl;

    @Column(name = "session_timeout")
    private Integer sessionTimeout;

    @Column(name = "password_min_length")
    private Integer passwordMinLength;

    @Column(name = "two_factor_required")
    private Boolean twoFactorRequired;

    @Column(name = "email_notifications")
    private Boolean emailNotifications;

    @Column(name = "sms_notifications")
    private Boolean smsNotifications;

    @Column(name = "notification_batch_size")
    private Integer notificationBatchSize;

    @Column(name = "api_rate_limit")
    private Integer apiRateLimit;

    @Column(name = "webhook_retries")
    private Integer webhookRetries;

    // Constructors
    public SystemSetting() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public String getPlatformUrl() { return platformUrl; }
    public void setPlatformUrl(String platformUrl) { this.platformUrl = platformUrl; }

    public String getPlatformLogo() { return platformLogo; }
    public void setPlatformLogo(String platformLogo) { this.platformLogo = platformLogo; }

    public String getDefaultLanguage() { return defaultLanguage; }
    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }

    public Boolean getMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(Boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public String getMaintenanceMessage() { return maintenanceMessage; }
    public void setMaintenanceMessage(String maintenanceMessage) { this.maintenanceMessage = maintenanceMessage; }

    public Integer getFileUploadMaxSize() { return fileUploadMaxSize; }
    public void setFileUploadMaxSize(Integer fileUploadMaxSize) { this.fileUploadMaxSize = fileUploadMaxSize; }

    public Boolean getBackupEnabled() { return backupEnabled; }
    public void setBackupEnabled(Boolean backupEnabled) { this.backupEnabled = backupEnabled; }

    public String getBackupFrequency() { return backupFrequency; }
    public void setBackupFrequency(String backupFrequency) { this.backupFrequency = backupFrequency; }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }

    public String getSupportPhone() { return supportPhone; }
    public void setSupportPhone(String supportPhone) { this.supportPhone = supportPhone; }

    public String getTermsUrl() { return termsUrl; }
    public void setTermsUrl(String termsUrl) { this.termsUrl = termsUrl; }

    public String getPrivacyUrl() { return privacyUrl; }
    public void setPrivacyUrl(String privacyUrl) { this.privacyUrl = privacyUrl; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getMaxConcurrentUsers() { return maxConcurrentUsers; }
    public void setMaxConcurrentUsers(Integer maxConcurrentUsers) { this.maxConcurrentUsers = maxConcurrentUsers; }

    public Boolean getCacheEnabled() { return cacheEnabled; }
    public void setCacheEnabled(Boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }

    public Integer getCacheTtl() { return cacheTtl; }
    public void setCacheTtl(Integer cacheTtl) { this.cacheTtl = cacheTtl; }

    public Integer getSessionTimeout() { return sessionTimeout; }
    public void setSessionTimeout(Integer sessionTimeout) { this.sessionTimeout = sessionTimeout; }

    public Integer getPasswordMinLength() { return passwordMinLength; }
    public void setPasswordMinLength(Integer passwordMinLength) { this.passwordMinLength = passwordMinLength; }

    public Boolean getTwoFactorRequired() { return twoFactorRequired; }
    public void setTwoFactorRequired(Boolean twoFactorRequired) { this.twoFactorRequired = twoFactorRequired; }

    public Boolean getEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(Boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public Boolean getSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(Boolean smsNotifications) { this.smsNotifications = smsNotifications; }

    public Integer getNotificationBatchSize() { return notificationBatchSize; }
    public void setNotificationBatchSize(Integer notificationBatchSize) { this.notificationBatchSize = notificationBatchSize; }

    public Integer getApiRateLimit() { return apiRateLimit; }
    public void setApiRateLimit(Integer apiRateLimit) { this.apiRateLimit = apiRateLimit; }

    public Integer getWebhookRetries() { return webhookRetries; }
    public void setWebhookRetries(Integer webhookRetries) { this.webhookRetries = webhookRetries; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
