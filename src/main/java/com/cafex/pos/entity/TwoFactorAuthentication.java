package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "two_factor_authentication")
public class TwoFactorAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tfa_id", nullable = false, unique = true)
    private String tfaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = false;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "secret_key")
    private String secretKey;

    @ElementCollection
    @CollectionTable(name = "tfa_backup_codes", joinColumns = @JoinColumn(name = "tfa_id"))
    @Column(name = "backup_code")
    private List<String> backupCodes;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public TwoFactorAuthentication() {}

    public TwoFactorAuthentication(String tfaId, User user, String method) {
        this.tfaId = tfaId;
        this.user = user;
        this.method = method;
        this.isEnabled = false;
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

    public String getTfaId() {
        return tfaId;
    }

    public void setTfaId(String tfaId) {
        this.tfaId = tfaId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public List<String> getBackupCodes() {
        return backupCodes;
    }

    public void setBackupCodes(List<String> backupCodes) {
        this.backupCodes = backupCodes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean isVerified() {
        return verifiedAt != null;
    }

    public void markAsVerified() {
        this.verifiedAt = LocalDateTime.now();
        this.isEnabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastUsed() {
        this.lastUsed = LocalDateTime.now();
    }

    public boolean hasBackupCodes() {
        return backupCodes != null && !backupCodes.isEmpty();
    }

    public boolean useBackupCode(String code) {
        if (backupCodes != null && backupCodes.contains(code)) {
            backupCodes.remove(code);
            this.lastUsed = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public void regenerateBackupCodes(List<String> newCodes) {
        this.backupCodes = newCodes;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        this.isEnabled = false;
        this.updatedAt = LocalDateTime.now();
    }
}
