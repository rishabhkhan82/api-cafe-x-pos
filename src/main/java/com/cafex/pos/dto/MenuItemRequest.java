package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Slf4j
public class MenuItemRequest {

    @NotBlank(message = "Item ID is required")
    @JsonProperty("item_id")
    private String itemId;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @JsonProperty("name")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @JsonProperty("price")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Original price must be greater than 0")
    @JsonProperty("original_price")
    private BigDecimal originalPrice;

    @NotBlank(message = "Category is required")
    @Size(max = 255, message = "Category must not exceed 255 characters")
    @JsonProperty("category")
    private String category;

    @JsonProperty("image")
    private String image;

    @NotNull(message = "Is available is required")
    @JsonProperty("is_available")
    private Boolean isAvailable = true;

    @JsonProperty("is_active")
    private Boolean isActive = true;

    @NotNull(message = "Is vegetarian is required")
    @JsonProperty("is_vegetarian")
    private Boolean isVegetarian = false;

    @JsonProperty("is_veg")
    private Boolean isVeg;

    @NotNull(message = "Is spicy is required")
    @JsonProperty("is_spicy")
    private Boolean isSpicy = false;

    @JsonProperty("is_popular")
    private Boolean isPopular = false;

    @NotNull(message = "Preparation time is required")
    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @JsonProperty("preparation_time")
    private Integer preparationTime;

    @JsonProperty("discount")
    private String discount;

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
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
}