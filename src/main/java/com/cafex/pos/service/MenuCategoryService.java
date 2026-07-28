package com.cafex.pos.service;

import com.cafex.pos.dto.MenuCategoryPageResponse;
import com.cafex.pos.dto.MenuCategoryRequest;
import com.cafex.pos.dto.MenuCategoryResponse;
import java.util.List;
import java.util.Optional;

public interface MenuCategoryService {
    MenuCategoryPageResponse getMenuCategoriesWithFilters(String name, Boolean isActive, int page, int size);
    List<MenuCategoryResponse> getAllMenuCategories();
    Optional<MenuCategoryResponse> getMenuCategoryById(Long id);
    MenuCategoryResponse createMenuCategory(MenuCategoryRequest request);
    MenuCategoryResponse updateMenuCategory(Long id, MenuCategoryRequest request);
    void deleteMenuCategory(Long id);
}
