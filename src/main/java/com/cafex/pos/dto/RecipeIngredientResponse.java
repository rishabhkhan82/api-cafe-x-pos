package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecipeIngredientResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("recipe_id")
    private Long recipeId;

    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;

    @JsonProperty("ingredient_id")
    private String ingredientId;

    @JsonProperty("ingredient_name")
    private String ingredientName;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("cost")
    private BigDecimal cost;

    @JsonProperty("is_optional")
    private Boolean isOptional;

    @JsonProperty("substitute_allowed")
    private Boolean substituteAllowed;

    @JsonProperty("substitute_ingredient")
    private String substituteIngredient;

    @JsonProperty("preparation_notes")
    private String preparationNotes;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
