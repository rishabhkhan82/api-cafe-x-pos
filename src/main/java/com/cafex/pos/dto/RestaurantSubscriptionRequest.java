package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Slf4j
public class RestaurantSubscriptionRequest {

    @NotBlank(message = "Subscription ID is required")
    @JsonProperty("subscription_id")
    private String subscriptionId;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @NotNull(message = "Plan ID is required")
    @JsonProperty("plan_id")
    private Long planId;

    @NotBlank(message = "Status is required")
    @JsonProperty("status")
    private String status;

    @NotNull(message = "Start date is required")
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("trial_start_date")
    private LocalDateTime trialStartDate;

    @JsonProperty("trial_end_date")
    private LocalDateTime trialEndDate;

    @JsonProperty("is_trial_used")
    private Boolean isTrialUsed = false;

    @JsonProperty("next_billing_date")
    private LocalDateTime nextBillingDate;

    @NotBlank(message = "Billing cycle is required")
    @JsonProperty("billing_cycle")
    private String billingCycle;

    @JsonProperty("current_period_start")
    private LocalDateTime currentPeriodStart;

    @JsonProperty("current_period_end")
    private LocalDateTime currentPeriodEnd;

    @NotNull(message = "Cancel at period end is required")
    @JsonProperty("cancel_at_period_end")
    private Boolean cancelAtPeriodEnd = false;

    @JsonProperty("cancelled_at")
    private LocalDateTime cancelledAt;

    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    @NotNull(message = "Auto renew is required")
    @JsonProperty("auto_renew")
    private Boolean autoRenew = true;

    @JsonProperty("discount_code")
    private String discountCode;

    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be 0 or greater")
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull(message = "Final amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Final amount must be 0 or greater")
    @JsonProperty("final_amount")
    private BigDecimal finalAmount = BigDecimal.ZERO;

    // For updates - optional fields
    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;

    // Additional fields as per table columns
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("plan_price_at_subscription")
    private BigDecimal planPriceAtSubscription;

    @JsonProperty("offer_name_at_subscription")
    private String offerNameAtSubscription;

    @JsonProperty("offer_discount_percentage_at_subscription")
    private Integer offerDiscountPercentageAtSubscription;

    @JsonProperty("plan_name_at_subscription")
    private String planNameAtSubscription;
}