package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WasteManagementRequest {

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("note")
    private String note;

    @JsonProperty("waste_date")
    private LocalDateTime wasteDate;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("waste_type")
    private String wasteType;

    @JsonProperty("recipe_id")
    private Long recipeId;

    @JsonProperty("waste_cost")
    private BigDecimal wasteCost;
}
