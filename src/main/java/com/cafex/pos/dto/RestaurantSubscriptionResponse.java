package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RestaurantSubscriptionResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("subscription_id")
    private String subscriptionId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("plan_id")
    private Long planId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("trial_start_date")
    private LocalDateTime trialStartDate;

    @JsonProperty("trial_end_date")
    private LocalDateTime trialEndDate;

    @JsonProperty("is_trial_used")
    private Boolean isTrialUsed;

    @JsonProperty("next_billing_date")
    private LocalDateTime nextBillingDate;

    @JsonProperty("billing_cycle")
    private String billingCycle;

    @JsonProperty("current_period_start")
    private LocalDateTime currentPeriodStart;

    @JsonProperty("current_period_end")
    private LocalDateTime currentPeriodEnd;

    @JsonProperty("cancel_at_period_end")
    private Boolean cancelAtPeriodEnd;

    @JsonProperty("cancelled_at")
    private LocalDateTime cancelledAt;

    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    @JsonProperty("auto_renew")
    private Boolean autoRenew;

    @JsonProperty("discount_code")
    private String discountCode;

    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @JsonProperty("final_amount")
    private BigDecimal finalAmount;

    @JsonProperty("gst_amount")
    private BigDecimal gstAmount;

    @JsonProperty("gst_percentage")
    private String gstPercentage;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;

    @JsonProperty("plan_price_at_subscription")
    private BigDecimal planPriceAtSubscription;

    @JsonProperty("offer_name_at_subscription")
    private String offerNameAtSubscription;

    @JsonProperty("offer_discount_percentage_at_subscription")
    private Integer offerDiscountPercentageAtSubscription;

    @JsonProperty("plan_name_at_subscription")
    private String planNameAtSubscription;
}