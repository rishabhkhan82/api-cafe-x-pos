package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MenuItemResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("original_price")
    private BigDecimal originalPrice;

    @JsonProperty("category")
    private String category;

    @JsonProperty("image")
    private String image;

    @JsonProperty("is_available")
    private Boolean isAvailable;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_vegetarian")
    private Boolean isVegetarian;

    @JsonProperty("is_veg")
    private Boolean isVeg;

    @JsonProperty("is_spicy")
    private Boolean isSpicy;

    @JsonProperty("is_popular")
    private Boolean isPopular;

    @JsonProperty("is_featured")
    private Boolean isFeatured;

    @JsonProperty("is_recommended")
    private Boolean isRecommended;

    @JsonProperty("preparation_time")
    private Integer preparationTime;

    @JsonProperty("discount")
    private String discount;

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