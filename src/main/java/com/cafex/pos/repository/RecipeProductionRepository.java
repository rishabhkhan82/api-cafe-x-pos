package com.cafex.pos.repository;

import com.cafex.pos.entity.RecipeProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeProductionRepository extends JpaRepository<RecipeProduction, Long>, JpaSpecificationExecutor<RecipeProduction> {
    List<RecipeProduction> findByRestaurantId(Long restaurantId);
    Page<RecipeProduction> findByRestaurantId(Long restaurantId, Pageable pageable);
    List<RecipeProduction> findByRestaurantIdAndNoteContainingIgnoreCase(Long restaurantId, String note);
    Page<RecipeProduction> findByRestaurantIdAndNoteContainingIgnoreCase(Long restaurantId, String note, Pageable pageable);
}
