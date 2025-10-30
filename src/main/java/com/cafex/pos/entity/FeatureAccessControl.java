package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feature_access_control")
public class FeatureAccessControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_id", nullable = false, unique = true)
    private String accessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "feature_id", nullable = false)
    private String featureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private SubscriptionPlans plan;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "restrictions", columnDefinition = "TEXT")
    private String restrictions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "override_by")
    private User overrideBy;

    @Column(name = "override_reason", columnDefinition = "TEXT")
    private String overrideReason;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public FeatureAccessControl() {}

    public FeatureAccessControl(String accessId, Restaurant restaurant, String featureId, SubscriptionPlans plan, Boolean isEnabled) {
        this.accessId = accessId;
        this.restaurant = restaurant;
        this.featureId = featureId;
        this.plan = plan;
        this.isEnabled = isEnabled != null ? isEnabled : true;
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

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public SubscriptionPlans getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlans plan) {
        this.plan = plan;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public User getOverrideBy() {
        return overrideBy;
    }

    public void setOverrideBy(User overrideBy) {
        this.overrideBy = overrideBy;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
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
    public boolean isOverrideActive() {
        return overrideBy != null && (validUntil == null || LocalDateTime.now().isBefore(validUntil));
    }

    public boolean hasRestrictions() {
        return restrictions != null && !restrictions.trim().isEmpty();
    }

    public void grantOverride(User user, String reason, LocalDateTime validUntil) {
        this.overrideBy = user;
        this.overrideReason = reason;
        this.validUntil = validUntil;
        this.isEnabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void revokeOverride() {
        this.overrideBy = null;
        this.overrideReason = null;
        this.validUntil = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }
}
