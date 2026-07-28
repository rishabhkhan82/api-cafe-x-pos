package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("recipe_id")
    private String recipeId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("serving_size")
    private Integer servingSize;

    @JsonProperty("preparation_time_minutes")
    private Integer preparationTimeMinutes;

    @JsonProperty("cooking_time_minutes")
    private Integer cookingTimeMinutes;

    @JsonProperty("total_time_minutes")
    private Integer totalTimeMinutes;

    @JsonProperty("difficulty_level")
    private String difficultyLevel;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("menu_item_id")
    private Long menuItemId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("ingredients")
    private List<RecipeIngredientResponse> ingredients = new ArrayList<>();

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}
