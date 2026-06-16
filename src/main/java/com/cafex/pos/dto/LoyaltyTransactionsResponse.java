package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoyaltyTransactionsResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("points")
    private Integer points;

    @JsonProperty("balance_before")
    private Integer balanceBefore;

    @JsonProperty("balance_after")
    private Integer balanceAfter;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("invoice_id")
    private String invoiceId;

    @JsonProperty("offer_id")
    private String offerId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;

    @JsonProperty("earned_from")
    private String earnedFrom;

    @JsonProperty("redeemed_for")
    private String redeemedFor;

    @JsonProperty("processed_by")
    private String processedBy;

    @JsonProperty("processed_at")
    private LocalDateTime processedAt;

    @JsonProperty("approval_required")
    private Boolean approvalRequired;

    @JsonProperty("approved_by")
    private String approvedBy;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @JsonProperty("reversal_transaction_id")
    private String reversalTransactionId;

    @JsonProperty("is_reversal")
    private Boolean isReversal;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
