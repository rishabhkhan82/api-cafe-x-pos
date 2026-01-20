package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false, unique = true)
    private String planId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle;

    @Column(name = "max_restaurants", nullable = false)
    private Integer maxRestaurants;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @ElementCollection
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature_id")
    private List<String> features;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_popular", nullable = false)
    private Boolean isPopular = false;

    @Column(name = "subscriber_count", nullable = false)
    private Integer subscriberCount = 0;

    @Column(name = "revenue", nullable = false)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "trial_days", nullable = false)
    private Integer trialDays = 0;

    @Column(name = "setup_fee", nullable = false)
    private BigDecimal setupFee = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SubscriptionPlans() {}

    public SubscriptionPlans(String planId, String name, String displayName, BigDecimal price, String currency, String billingCycle, Integer maxRestaurants, Integer maxUsers) {
        this.planId = planId;
        this.name = name;
        this.displayName = displayName;
        this.price = price;
        this.currency = currency;
        this.billingCycle = billingCycle;
        this.maxRestaurants = maxRestaurants;
        this.maxUsers = maxUsers;
        this.isActive = true;
        this.isPopular = false;
        this.subscriberCount = 0;
        this.revenue = BigDecimal.ZERO;
        this.trialDays = 0;
        this.setupFee = BigDecimal.ZERO;
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

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Integer getMaxRestaurants() {
        return maxRestaurants;
    }

    public void setMaxRestaurants(Integer maxRestaurants) {
        this.maxRestaurants = maxRestaurants;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsPopular() {
        return isPopular;
    }

    public void setIsPopular(Boolean isPopular) {
        this.isPopular = isPopular;
    }

    public Integer getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(Integer subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Integer getTrialDays() {
        return trialDays;
    }

    public void setTrialDays(Integer trialDays) {
        this.trialDays = trialDays;
    }

    public BigDecimal getSetupFee() {
        return setupFee;
    }

    public void setSetupFee(BigDecimal setupFee) {
        this.setupFee = setupFee;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
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
}
