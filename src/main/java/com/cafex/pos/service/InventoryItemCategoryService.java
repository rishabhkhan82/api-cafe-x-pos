package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemCategoryPageResponse;
import com.cafex.pos.dto.InventoryItemCategoryRequest;
import com.cafex.pos.dto.InventoryItemCategoryResponse;
import java.util.List;
import java.util.Optional;

public interface InventoryItemCategoryService {
    InventoryItemCategoryPageResponse getInventoryItemCategoriesWithFilters(String name, Boolean isActive, int page, int size);
    List<InventoryItemCategoryResponse> getAllInventoryItemCategories();
    Optional<InventoryItemCategoryResponse> getInventoryItemCategoryById(Long id);
    InventoryItemCategoryResponse createInventoryItemCategory(InventoryItemCategoryRequest request);
    InventoryItemCategoryResponse updateInventoryItemCategory(Long id, InventoryItemCategoryRequest request);
    void deleteInventoryItemCategory(Long id);
}
