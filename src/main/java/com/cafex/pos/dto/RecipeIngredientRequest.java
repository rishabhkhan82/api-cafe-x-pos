package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecipeIngredientRequest {

    @JsonProperty("ingredient_id")
    private String ingredientId;

    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;

    @JsonProperty("ingredient_name")
    private String ingredientName;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("cost")
    private BigDecimal cost;

    @JsonProperty("is_optional")
    private Boolean isOptional = false;

    @JsonProperty("substitute_allowed")
    private Boolean substituteAllowed = false;

    @JsonProperty("substitute_ingredient")
    private String substituteIngredient;

    @JsonProperty("preparation_notes")
    private String preparationNotes;
}
