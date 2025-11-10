package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_history")
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "history_id", nullable = false, unique = true)
    private String historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "previous_plan_id")
    private String previousPlanId;

    @Column(name = "new_plan_id", nullable = false)
    private String newPlanId;

    @Column(name = "change_type", nullable = false)
    private String changeType;

    @Column(name = "effective_date", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "previous_price")
    private BigDecimal previousPrice;

    @Column(name = "new_price")
    private BigDecimal newPrice;

    @Column(name = "price_difference")
    private BigDecimal priceDifference;

    @Column(name = "billing_cycle_change", nullable = false)
    private Boolean billingCycleChange = false;

    @Column(name = "prorated_amount")
    private BigDecimal proratedAmount;

    @Column(name = "initiated_by")
    private String initiatedBy;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "churn_risk_score")
    private BigDecimal churnRiskScore;

    @Column(name = "retention_actions", columnDefinition = "JSON")
    private String retentionActions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public SubscriptionHistory() {}

    public SubscriptionHistory(String historyId, Restaurant restaurant, String newPlanId, String changeType, LocalDateTime effectiveDate) {
        this.historyId = historyId;
        this.restaurant = restaurant;
        this.newPlanId = newPlanId;
        this.changeType = changeType;
        this.effectiveDate = effectiveDate;
        this.billingCycleChange = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getPreviousPlanId() {
        return previousPlanId;
    }

    public void setPreviousPlanId(String previousPlanId) {
        this.previousPlanId = previousPlanId;
    }

    public String getNewPlanId() {
        return newPlanId;
    }

    public void setNewPlanId(String newPlanId) {
        this.newPlanId = newPlanId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(BigDecimal previousPrice) {
        this.previousPrice = previousPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public Boolean getBillingCycleChange() {
        return billingCycleChange;
    }

    public void setBillingCycleChange(Boolean billingCycleChange) {
        this.billingCycleChange = billingCycleChange;
    }

    public BigDecimal getProratedAmount() {
        return proratedAmount;
    }

    public void setProratedAmount(BigDecimal proratedAmount) {
        this.proratedAmount = proratedAmount;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public BigDecimal getChurnRiskScore() {
        return churnRiskScore;
    }

    public void setChurnRiskScore(BigDecimal churnRiskScore) {
        this.churnRiskScore = churnRiskScore;
    }

    public String getRetentionActions() {
        return retentionActions;
    }

    public void setRetentionActions(String retentionActions) {
        this.retentionActions = retentionActions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
