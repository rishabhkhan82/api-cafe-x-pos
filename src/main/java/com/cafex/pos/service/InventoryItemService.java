package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemRequest;
import com.cafex.pos.dto.InventoryItemResponse;
import com.cafex.pos.dto.InventoryItemPageResponse;

import java.util.Optional;

public interface InventoryItemService {
    InventoryItemResponse saveInventoryItem(InventoryItemRequest inventoryItemRequest);
    InventoryItemResponse updateInventoryItem(Long id, InventoryItemRequest inventoryItemRequest);
    InventoryItemPageResponse getInventoryItemsWithFilters(String name, String category, String unitOfMeasure, String restaurantId, String isActive, int page, int size);
    Optional<InventoryItemResponse> getInventoryItemById(Long id);
    void deleteInventoryItem(Long id);
}
