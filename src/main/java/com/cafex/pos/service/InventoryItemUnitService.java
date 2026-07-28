package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemUnitPageResponse;
import com.cafex.pos.dto.InventoryItemUnitRequest;
import com.cafex.pos.dto.InventoryItemUnitResponse;
import java.util.List;
import java.util.Optional;

public interface InventoryItemUnitService {
    InventoryItemUnitPageResponse getInventoryItemUnitsWithFilters(String name, Boolean isActive, int page, int size);
    List<InventoryItemUnitResponse> getAllInventoryItemUnits();
    Optional<InventoryItemUnitResponse> getInventoryItemUnitById(Long id);
    InventoryItemUnitResponse createInventoryItemUnit(InventoryItemUnitRequest request);
    InventoryItemUnitResponse updateInventoryItemUnit(Long id, InventoryItemUnitRequest request);
    void deleteInventoryItemUnit(Long id);
}
