package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Slf4j
public class SubscriptionHistoryRequest {

    @NotBlank(message = "History ID is required")
    @JsonProperty("history_id")
    private String historyId;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("previous_plan_id")
    private String previousPlanId;

    @NotBlank(message = "New plan ID is required")
    @JsonProperty("new_plan_id")
    private String newPlanId;

    @NotBlank(message = "Change type is required")
    @JsonProperty("change_type")
    private String changeType;

    @NotNull(message = "Effective date is required")
    @JsonProperty("effective_date")
    private LocalDateTime effectiveDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Previous price must be 0 or greater")
    @JsonProperty("previous_price")
    private BigDecimal previousPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "New price must be 0 or greater")
    @JsonProperty("new_price")
    private BigDecimal newPrice;

    @JsonProperty("price_difference")
    private BigDecimal priceDifference;

    @NotNull(message = "Billing cycle change is required")
    @JsonProperty("billing_cycle_change")
    private Boolean billingCycleChange = false;

    @DecimalMin(value = "0.0", inclusive = true, message = "Prorated amount must be 0 or greater")
    @JsonProperty("prorated_amount")
    private BigDecimal proratedAmount;

    @JsonProperty("initiated_by")
    private String initiatedBy;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    @JsonProperty("churn_risk_score")
    private BigDecimal churnRiskScore;

    @JsonProperty("retention_actions")
    private String retentionActions;

    // For updates - optional fields
    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}