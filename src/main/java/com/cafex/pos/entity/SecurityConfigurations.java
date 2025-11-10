package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_configurations")
public class SecurityConfigurations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_timeout", nullable = false)
    private Integer sessionTimeout = 30;

    @Column(name = "password_min_length", nullable = false)
    private Integer passwordMinLength = 8;

    @Column(name = "password_require_special_chars", nullable = false)
    private Boolean passwordRequireSpecialChars = false;

    @Column(name = "password_require_numbers", nullable = false)
    private Boolean passwordRequireNumbers = true;

    @Column(name = "max_login_attempts", nullable = false)
    private Integer maxLoginAttempts = 5;

    @Column(name = "lockout_duration", nullable = false)
    private Integer lockoutDuration = 15;

    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column(name = "audit_log_retention", nullable = false)
    private Integer auditLogRetention = 90;

    @Column(name = "ip_whitelist", columnDefinition = "JSON")
    private String ipWhitelist;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public SecurityConfigurations() {}

    public SecurityConfigurations(Integer sessionTimeout, Integer passwordMinLength,
                                Boolean passwordRequireSpecialChars, Boolean passwordRequireNumbers,
                                Integer maxLoginAttempts, Integer lockoutDuration,
                                Boolean twoFactorEnabled, Integer auditLogRetention) {
        this.sessionTimeout = sessionTimeout != null ? sessionTimeout : 30;
        this.passwordMinLength = passwordMinLength != null ? passwordMinLength : 8;
        this.passwordRequireSpecialChars = passwordRequireSpecialChars != null ? passwordRequireSpecialChars : false;
        this.passwordRequireNumbers = passwordRequireNumbers != null ? passwordRequireNumbers : true;
        this.maxLoginAttempts = maxLoginAttempts != null ? maxLoginAttempts : 5;
        this.lockoutDuration = lockoutDuration != null ? lockoutDuration : 15;
        this.twoFactorEnabled = twoFactorEnabled != null ? twoFactorEnabled : false;
        this.auditLogRetention = auditLogRetention != null ? auditLogRetention : 90;
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

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Integer getPasswordMinLength() {
        return passwordMinLength;
    }

    public void setPasswordMinLength(Integer passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public Boolean getPasswordRequireSpecialChars() {
        return passwordRequireSpecialChars;
    }

    public void setPasswordRequireSpecialChars(Boolean passwordRequireSpecialChars) {
        this.passwordRequireSpecialChars = passwordRequireSpecialChars;
    }

    public Boolean getPasswordRequireNumbers() {
        return passwordRequireNumbers;
    }

    public void setPasswordRequireNumbers(Boolean passwordRequireNumbers) {
        this.passwordRequireNumbers = passwordRequireNumbers;
    }

    public Integer getMaxLoginAttempts() {
        return maxLoginAttempts;
    }

    public void setMaxLoginAttempts(Integer maxLoginAttempts) {
        this.maxLoginAttempts = maxLoginAttempts;
    }

    public Integer getLockoutDuration() {
        return lockoutDuration;
    }

    public void setLockoutDuration(Integer lockoutDuration) {
        this.lockoutDuration = lockoutDuration;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Integer getAuditLogRetention() {
        return auditLogRetention;
    }

    public void setAuditLogRetention(Integer auditLogRetention) {
        this.auditLogRetention = auditLogRetention;
    }

    public String getIpWhitelist() {
        return ipWhitelist;
    }

    public void setIpWhitelist(String ipWhitelist) {
        this.ipWhitelist = ipWhitelist;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
}
