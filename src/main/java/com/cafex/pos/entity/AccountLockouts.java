package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "account_lockouts")
public class AccountLockouts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lockout_id", nullable = false, unique = true)
    private String lockoutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "lockout_start", nullable = false)
    private LocalDateTime lockoutStart;

    @Column(name = "lockout_end", nullable = false)
    private LocalDateTime lockoutEnd;

    @Column(nullable = false)
    private String reason;

    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts;

    @ElementCollection
    @CollectionTable(name = "lockout_ip_addresses", joinColumns = @JoinColumn(name = "lockout_id"))
    @Column(name = "ip_address")
    private List<String> ipAddresses;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unlocked_by")
    private User unlockedBy;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public AccountLockouts() {}

    public AccountLockouts(String lockoutId, User user, LocalDateTime lockoutStart, LocalDateTime lockoutEnd, String reason, Integer failedAttempts) {
        this.lockoutId = lockoutId;
        this.user = user;
        this.lockoutStart = lockoutStart;
        this.lockoutEnd = lockoutEnd;
        this.reason = reason;
        this.failedAttempts = failedAttempts;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLockoutId() {
        return lockoutId;
    }

    public void setLockoutId(String lockoutId) {
        this.lockoutId = lockoutId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLockoutStart() {
        return lockoutStart;
    }

    public void setLockoutStart(LocalDateTime lockoutStart) {
        this.lockoutStart = lockoutStart;
    }

    public LocalDateTime getLockoutEnd() {
        return lockoutEnd;
    }

    public void setLockoutEnd(LocalDateTime lockoutEnd) {
        this.lockoutEnd = lockoutEnd;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public User getUnlockedBy() {
        return unlockedBy;
    }

    public void setUnlockedBy(User unlockedBy) {
        this.unlockedBy = unlockedBy;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isCurrentlyLocked() {
        return isActive && LocalDateTime.now().isBefore(lockoutEnd);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(lockoutEnd);
    }

    public long getMinutesRemaining() {
        if (!isCurrentlyLocked()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), lockoutEnd).toMinutes();
    }

    public void unlock(User unlockedBy) {
        this.isActive = false;
        this.unlockedBy = unlockedBy;
        this.unlockedAt = LocalDateTime.now();
    }

    public boolean canBeUnlocked() {
        return isActive && isExpired();
    }

    public void extendLockout(LocalDateTime newEndTime) {
        this.lockoutEnd = newEndTime;
    }
}
