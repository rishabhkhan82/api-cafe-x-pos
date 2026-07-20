package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_subscriptions")
public class RestaurantSubscriptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id", nullable = false, unique = true)
    private String subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlans plan;

    @Column(nullable = false)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "trial_start_date")
    private LocalDateTime trialStartDate;

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Column(name = "is_trial_used", nullable = false)
    private Boolean isTrialUsed = false;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle;

    @Column(name = "current_period_start")
    private LocalDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private LocalDateTime currentPeriodEnd;

    @Column(name = "cancel_at_period_end", nullable = false)
    private Boolean cancelAtPeriodEnd = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "payment_method_id")
    private String paymentMethodId;

    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    @Column(name = "discount_code")
    private String discountCode;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(name = "gst_amount", nullable = false)
    private BigDecimal gstAmount = BigDecimal.ZERO;

    @Column(name = "gst_percentage", nullable = false)
    private String gstPercentage = "0";

    @Column(name = "plan_price_at_subscription", nullable = false)
    private BigDecimal planPriceAtSubscription = BigDecimal.ZERO;

    @Column(name = "offer_name_at_subscription")
    private String offerNameAtSubscription;

    @Column(name = "offer_discount_percentage_at_subscription", nullable = false)
    private Integer offerDiscountPercentageAtSubscription = 0;

    @Column(name = "plan_name_at_subscription", nullable = false)
    private String planNameAtSubscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public RestaurantSubscriptions() {}

    public RestaurantSubscriptions(String subscriptionId, Restaurant restaurant, SubscriptionPlans plan, String status, LocalDateTime startDate, String billingCycle) {
        this.subscriptionId = subscriptionId;
        this.restaurant = restaurant;
        this.plan = plan;
        this.status = status;
        this.startDate = startDate;
        this.billingCycle = billingCycle;
        this.cancelAtPeriodEnd = false;
        this.autoRenew = true;
        this.isTrialUsed = false;
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
        this.planPriceAtSubscription = BigDecimal.ZERO;
        this.offerDiscountPercentageAtSubscription = 0;
        this.planNameAtSubscription = "";
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

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public SubscriptionPlans getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlans plan) {
        this.plan = plan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getTrialStartDate() {
        return trialStartDate;
    }

    public void setTrialStartDate(LocalDateTime trialStartDate) {
        this.trialStartDate = trialStartDate;
    }

    public LocalDateTime getTrialEndDate() {
        return trialEndDate;
    }

    public void setTrialEndDate(LocalDateTime trialEndDate) {
        this.trialEndDate = trialEndDate;
    }

    public Boolean getIsTrialUsed() {
        return isTrialUsed;
    }

    public void setIsTrialUsed(Boolean isTrialUsed) {
        this.isTrialUsed = isTrialUsed;
    }

    public LocalDateTime getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDateTime nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public LocalDateTime getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public void setCurrentPeriodStart(LocalDateTime currentPeriodStart) {
        this.currentPeriodStart = currentPeriodStart;
    }

    public LocalDateTime getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(LocalDateTime currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public Boolean getCancelAtPeriodEnd() {
        return cancelAtPeriodEnd;
    }

    public void setCancelAtPeriodEnd(Boolean cancelAtPeriodEnd) {
        this.cancelAtPeriodEnd = cancelAtPeriodEnd;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public String getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(String gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public BigDecimal getPlanPriceAtSubscription() {
        return planPriceAtSubscription;
    }

    public void setPlanPriceAtSubscription(BigDecimal planPriceAtSubscription) {
        this.planPriceAtSubscription = planPriceAtSubscription;
    }

    public String getOfferNameAtSubscription() {
        return offerNameAtSubscription;
    }

    public void setOfferNameAtSubscription(String offerNameAtSubscription) {
        this.offerNameAtSubscription = offerNameAtSubscription;
    }

    public Integer getOfferDiscountPercentageAtSubscription() {
        return offerDiscountPercentageAtSubscription;
    }

    public void setOfferDiscountPercentageAtSubscription(Integer offerDiscountPercentageAtSubscription) {
        this.offerDiscountPercentageAtSubscription = offerDiscountPercentageAtSubscription;
    }

    public String getPlanNameAtSubscription() {
        return planNameAtSubscription;
    }

    public void setPlanNameAtSubscription(String planNameAtSubscription) {
        this.planNameAtSubscription = planNameAtSubscription;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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
