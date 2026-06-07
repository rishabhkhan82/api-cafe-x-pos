package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OfferRedemptionResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("redemption_id")
    private String redemptionId;

    @JsonProperty("offer_id")
    private Long offerId;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("redemption_code")
    private String redemptionCode;

    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @JsonProperty("original_amount")
    private BigDecimal originalAmount;

    @JsonProperty("final_amount")
    private BigDecimal finalAmount;

    @JsonProperty("redemption_method")
    private String redemptionMethod;

    @JsonProperty("applied_by")
    private String appliedBy;

    @JsonProperty("applied_at")
    private LocalDateTime appliedAt;

    @JsonProperty("order_items")
    private String orderItems;

    @JsonProperty("conditions_met")
    private String conditionsMet;

    @JsonProperty("usage_count")
    private Integer usageCount;

    @JsonProperty("device_type")
    private String deviceType;

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("location")
    private String location;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
