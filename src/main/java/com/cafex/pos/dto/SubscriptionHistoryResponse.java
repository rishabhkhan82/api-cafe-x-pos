package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubscriptionHistoryResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("history_id")
    private String historyId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("previous_plan_id")
    private String previousPlanId;

    @JsonProperty("new_plan_id")
    private String newPlanId;

    @JsonProperty("change_type")
    private String changeType;

    @JsonProperty("effective_date")
    private LocalDateTime effectiveDate;

    @JsonProperty("previous_price")
    private BigDecimal previousPrice;

    @JsonProperty("new_price")
    private BigDecimal newPrice;

    @JsonProperty("price_difference")
    private BigDecimal priceDifference;

    @JsonProperty("billing_cycle_change")
    private Boolean billingCycleChange;

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

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}