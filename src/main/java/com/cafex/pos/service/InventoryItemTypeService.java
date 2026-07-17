package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemTypePageResponse;
import com.cafex.pos.dto.InventoryItemTypeRequest;
import com.cafex.pos.dto.InventoryItemTypeResponse;
import java.util.List;
import java.util.Optional;

public interface InventoryItemTypeService {
    InventoryItemTypePageResponse getInventoryItemTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<InventoryItemTypeResponse> getAllInventoryItemTypes();
    Optional<InventoryItemTypeResponse> getInventoryItemTypeById(Long id);
    InventoryItemTypeResponse createInventoryItemType(InventoryItemTypeRequest request);
    InventoryItemTypeResponse updateInventoryItemType(Long id, InventoryItemTypeRequest request);
    void deleteInventoryItemType(Long id);
}
