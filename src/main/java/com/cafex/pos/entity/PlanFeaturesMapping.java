package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_features_mapping",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_plan_feature", columnNames = {"plan_id", "feature_id"})
       },
       indexes = {
           @Index(name = "idx_plan_features_plan", columnList = "plan_id"),
           @Index(name = "idx_plan_features_feature", columnList = "feature_id"),
           @Index(name = "idx_plan_features_created_by", columnList = "created_by"),
           @Index(name = "idx_plan_features_updated_by", columnList = "updated_by")
       })
public class PlanFeaturesMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlans plan;

    @Column(name = "feature_id", nullable = false, length = 255)
    private String featureId;

    @Column(name = "is_enabled", nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean isEnabled = true;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    // Constructors
    public PlanFeaturesMapping() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public PlanFeaturesMapping(SubscriptionPlans plan, String featureId, Boolean isEnabled) {
        this.plan = plan;
        this.featureId = featureId;
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

    public SubscriptionPlans getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlans plan) {
        this.plan = plan;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    // Helper methods
    public boolean isEnabled() {
        return isEnabled != null && isEnabled;
    }

    public void enable() {
        this.isEnabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        this.isEnabled = false;
        this.updatedAt = LocalDateTime.now();
    }
}