package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecipeProductionResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("recipe_id")
    private Long recipeId;

    @JsonProperty("menu_item_id")
    private Long menuItemId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("batch_multiplier")
    private BigDecimal batchMultiplier;

    @JsonProperty("note")
    private String note;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
