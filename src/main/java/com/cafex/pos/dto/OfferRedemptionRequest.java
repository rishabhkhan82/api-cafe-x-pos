package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OfferRedemptionRequest {

    @JsonProperty("redemption_id")
    private String redemptionId;

    @NotNull(message = "Offer ID is required")
    @JsonProperty("offer_id")
    private Long offerId;

    @NotNull(message = "Order ID is required")
    @JsonProperty("order_id")
    private Long orderId;

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Long customerId;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("redemption_code")
    private String redemptionCode;

    @NotNull(message = "Discount amount is required")
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @NotNull(message = "Original amount is required")
    @JsonProperty("original_amount")
    private BigDecimal originalAmount;

    @NotNull(message = "Final amount is required")
    @JsonProperty("final_amount")
    private BigDecimal finalAmount;

    @NotBlank(message = "Redemption method is required")
    @JsonProperty("redemption_method")
    private String redemptionMethod;

    @JsonProperty("applied_by")
    private String appliedBy;

    @JsonProperty("applied_at")
    private LocalDateTime appliedAt;

    @JsonProperty("invoice_id")
    private String invoiceId;

    @JsonProperty("order_items")
    private String orderItems;

    @JsonProperty("conditions_met")
    private String conditionsMet;

    @JsonProperty("device_type")
    private String deviceType;

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("location")
    private String location;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
