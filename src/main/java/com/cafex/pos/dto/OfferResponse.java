package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OfferResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("offer_id")
    private String offerId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("discount_value")
    private BigDecimal discountValue;

    @JsonProperty("min_order_value")
    private BigDecimal minOrderValue;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("usage_limit")
    private Integer usageLimit;

    @JsonProperty("usage_count")
    private Integer usageCount;

    @JsonProperty("max_usage_per_customer")
    private Integer maxUsagePerCustomer;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("auto_apply")
    private Boolean autoApply;

    @JsonProperty("code")
    private String code;

    @JsonProperty("terms")
    private String terms;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}