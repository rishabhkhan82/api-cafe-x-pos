package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoyaltyTransactionsRequest {

    @JsonProperty("transaction_id")
    @Size(max = 255, message = "Transaction ID must not exceed 255 characters")
    private String transactionId;

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Long customerId;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @NotBlank(message = "Transaction type is required")
    @JsonProperty("transaction_type")
    private String transactionType;

    @NotNull(message = "Points is required")
    @JsonProperty("points")
    private Integer points;

    @NotNull(message = "Balance before is required")
    @JsonProperty("balance_before")
    private Integer balanceBefore;

    @NotNull(message = "Balance after is required")
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

    @NotNull(message = "Approval required is required")
    @JsonProperty("approval_required")
    private Boolean approvalRequired = false;

    @JsonProperty("approved_by")
    private String approvedBy;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @JsonProperty("reversal_transaction_id")
    private String reversalTransactionId;

    @NotNull(message = "Is reversal is required")
    @JsonProperty("is_reversal")
    private Boolean isReversal = false;

    @JsonProperty("notes")
    private String notes;

    @NotNull(message = "Created at is required")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}
