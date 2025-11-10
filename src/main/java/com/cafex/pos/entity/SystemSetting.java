package com.cafex.pos.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonRawValue;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
public class SystemSetting {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 20)
    private String type;

    @JsonRawValue
    @Column(columnDefinition = "JSON")
    private String value;

    @JsonRawValue
    @Column(name = "default_value", columnDefinition = "JSON")
    private String defaultValue;

    @JsonRawValue
    @Column(columnDefinition = "JSON")
    private String options;

    @JsonRawValue
    @Column(columnDefinition = "JSON")
    private String validation;

    @Column(name = "requires_restart", nullable = false)
    private Boolean requiresRestart = false;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "password_min_length")
    private Integer passwordMinLength;

    @Column(name = "two_factor_required", nullable = false)
    private Boolean twoFactorRequired = false;

    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications", nullable = false)
    private Boolean smsNotifications = false;

    @Column(name = "notification_batch_size")
    private Integer notificationBatchSize = 100;

    @Column(name = "webhook_retries")
    private Integer webhookRetries = 3;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SystemSetting() {}

    public SystemSetting(String id, String name, String description, String category,
                        String type, String value, String defaultValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
        this.value = value;
        this.defaultValue = defaultValue;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public String getValidation() { return validation; }
    public void setValidation(String validation) { this.validation = validation; }

    public Boolean getRequiresRestart() { return requiresRestart; }
    public void setRequiresRestart(Boolean requiresRestart) { this.requiresRestart = requiresRestart; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

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

    public Integer getWebhookRetries() { return webhookRetries; }
    public void setWebhookRetries(Integer webhookRetries) { this.webhookRetries = webhookRetries; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

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