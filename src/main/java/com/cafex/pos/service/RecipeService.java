package com.cafex.pos.service;

import com.cafex.pos.dto.RecipePageResponse;
import com.cafex.pos.dto.RecipeProductionResponse;
import com.cafex.pos.dto.RecipeRequest;
import com.cafex.pos.dto.RecipeResponse;
import com.cafex.pos.entity.RecipeProduction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RecipeService {
    RecipeResponse createRecipe(RecipeRequest request);
    RecipeResponse updateRecipe(Long id, RecipeRequest request);
    Optional<RecipeResponse> getRecipeById(Long id);
    List<RecipeResponse> getRecipesByRestaurant(Long restaurantId);
    RecipePageResponse getRecipesByRestaurant(Long restaurantId, Pageable pageable);
    RecipePageResponse getRecipesByRestaurant(Long restaurantId, Pageable pageable, String name);
    void deleteRecipe(Long id);
    boolean existsByRecipeId(String recipeId);
    void produceRecipe(Long menuItemId, Double batchMultiplier, String note, Long createdBy);
    RecipeProduction createProduction(Long recipeId, Long menuItemId, Long restaurantId, Double batchMultiplier, String note, Long createdBy);
    Map<String, Object> getRecipeProductionsByRestaurant(Long restaurantId, Pageable pageable, String name);
}
