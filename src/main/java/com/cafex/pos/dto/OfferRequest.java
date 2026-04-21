package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Slf4j
public class OfferRequest {

    @JsonProperty("offer_id")
    private String offerId;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Type is required")
    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("discount_value")
    private BigDecimal discountValue;

    @JsonProperty("min_order_value")
    private BigDecimal minOrderValue;

    @NotNull(message = "Start date is required")
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @NotNull(message = "Usage limit is required")
    @Min(value = 0, message = "Usage limit must be non-negative")
    @JsonProperty("usage_limit")
    private Integer usageLimit = 0;

    @JsonProperty("usage_count")
    private Integer usageCount = 0;

    @NotNull(message = "Max usage per customer is required")
    @Min(value = 1, message = "Max usage per customer must be at least 1")
    @JsonProperty("max_usage_per_customer")
    private Integer maxUsagePerCustomer = 1;

    @NotNull(message = "Is active is required")
    @JsonProperty("is_active")
    private Boolean isActive = true;

    @NotNull(message = "Auto apply is required")
    @JsonProperty("auto_apply")
    private Boolean autoApply = false;

    @JsonProperty("code")
    private String code;

    @JsonProperty("terms")
    private String terms;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

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
}