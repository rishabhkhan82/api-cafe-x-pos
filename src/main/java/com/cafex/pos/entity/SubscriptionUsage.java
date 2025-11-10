package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_usage")
public class SubscriptionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usage_id", nullable = false, unique = true)
    private String usageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private RestaurantSubscriptions subscription;

    @Column(nullable = false)
    private String metric;

    @Column(name = "current_value", nullable = false)
    private Integer currentValue = 0;

    @Column(name = "limit_value", nullable = false)
    private Integer limitValue;

    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;

    @Column(name = "is_approaching_limit", nullable = false)
    private Boolean isApproachingLimit = false;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public SubscriptionUsage() {}

    public SubscriptionUsage(String usageId, RestaurantSubscriptions subscription, String metric, Integer limitValue, LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.usageId = usageId;
        this.subscription = subscription;
        this.metric = metric;
        this.currentValue = 0;
        this.limitValue = limitValue;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.isApproachingLimit = false;
        this.lastUpdated = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsageId() {
        return usageId;
    }

    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

    public RestaurantSubscriptions getSubscription() {
        return subscription;
    }

    public void setSubscription(RestaurantSubscriptions subscription) {
        this.subscription = subscription;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
        this.lastUpdated = LocalDateTime.now();
        this.isApproachingLimit = this.limitValue != null && this.limitValue > 0 && this.currentValue >= (this.limitValue * 0.8);
    }

    public Integer getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(Integer limitValue) {
        this.limitValue = limitValue;
        this.isApproachingLimit = this.limitValue != null && this.limitValue > 0 && this.currentValue >= (this.limitValue * 0.8);
    }

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Boolean getIsApproachingLimit() {
        return isApproachingLimit;
    }

    public void setIsApproachingLimit(Boolean isApproachingLimit) {
        this.isApproachingLimit = isApproachingLimit;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isUnlimited() {
        return limitValue == null || limitValue == -1;
    }

    public boolean isLimitExceeded() {
        return !isUnlimited() && currentValue >= limitValue;
    }

    public double getUsagePercentage() {
        if (isUnlimited()) {
            return 0.0;
        }
        return (double) currentValue / limitValue * 100.0;
    }

    public void incrementUsage() {
        if (!isUnlimited()) {
            this.currentValue++;
            this.lastUpdated = LocalDateTime.now();
            this.isApproachingLimit = this.currentValue >= (this.limitValue * 0.8);
        }
    }

    public void incrementUsage(int amount) {
        if (!isUnlimited()) {
            this.currentValue += amount;
            this.lastUpdated = LocalDateTime.now();
            this.isApproachingLimit = this.currentValue >= (this.limitValue * 0.8);
        }
    }
}
