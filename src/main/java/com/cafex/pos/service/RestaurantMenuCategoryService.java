package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantMenuCategoryPageResponse;
import com.cafex.pos.dto.RestaurantMenuCategoryRequest;
import com.cafex.pos.dto.RestaurantMenuCategoryResponse;
import java.util.List;
import java.util.Optional;

public interface RestaurantMenuCategoryService {
    RestaurantMenuCategoryPageResponse getRestaurantMenuCategoriesWithFilters(Long restaurantId, String name, Boolean isActive, int page, int size);
    List<RestaurantMenuCategoryResponse> getAllRestaurantMenuCategories(Long restaurantId);
    List<RestaurantMenuCategoryResponse> getActiveRestaurantMenuCategories(Long restaurantId);
    Optional<RestaurantMenuCategoryResponse> getRestaurantMenuCategoryById(Long restaurantId, Long id);
    RestaurantMenuCategoryResponse createRestaurantMenuCategory(RestaurantMenuCategoryRequest request);
    RestaurantMenuCategoryResponse updateRestaurantMenuCategory(Long restaurantId, Long id, RestaurantMenuCategoryRequest request);
    void deleteRestaurantMenuCategory(Long restaurantId, Long id);
}
