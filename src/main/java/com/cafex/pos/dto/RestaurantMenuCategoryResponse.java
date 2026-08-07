package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RestaurantMenuCategoryResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("key")
    private String key;

    @JsonProperty("description")
    private String description;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("color")
    private String color;

    @JsonProperty("display_order")
    private Integer displayOrder;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_default")
    private Boolean isDefault;

    @JsonProperty("parent_category_id")
    private String parentCategoryId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("item_count")
    private Integer itemCount;

    @JsonProperty("total_value")
    private BigDecimal totalValue;

    @JsonProperty("popularity_score")
    private BigDecimal popularityScore;

    @JsonProperty("last_ordered")
    private LocalDateTime lastOrdered;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
