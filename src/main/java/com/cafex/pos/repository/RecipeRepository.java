package com.cafex.pos.repository;

import com.cafex.pos.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    boolean existsByRecipeId(String recipeId);
    Optional<Recipe> findByRecipeId(String recipeId);
    List<Recipe> findByRestaurantId(Long restaurantId);
    Page<Recipe> findByRestaurantId(Long restaurantId, Pageable pageable);
    Page<Recipe> findByRestaurantIdAndNameContainingIgnoreCase(Long restaurantId, String name, Pageable pageable);
}
