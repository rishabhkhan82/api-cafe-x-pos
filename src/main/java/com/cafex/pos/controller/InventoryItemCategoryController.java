package com.cafex.pos.controller;

import com.cafex.pos.dto.InventoryItemCategoryPageResponse;
import com.cafex.pos.dto.InventoryItemCategoryRequest;
import com.cafex.pos.dto.InventoryItemCategoryResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.InventoryItemCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-item-categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InventoryItemCategoryController {

    private final InventoryItemCategoryService inventoryItemCategoryService;

    @PostMapping
    public ResponseEntity<OperationResponse> createInventoryItemCategory(@Valid @RequestBody InventoryItemCategoryRequest request) {
        log.info("Create inventory item category request received for key: {}", request.getKey());
        InventoryItemCategoryResponse response = inventoryItemCategoryService.createInventoryItemCategory(request);
        log.info("Inventory item category created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_CATEGORY_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateInventoryItemCategory(@PathVariable Long id, @Valid @RequestBody InventoryItemCategoryRequest request) {
        log.info("Update inventory item category request received for ID: {}", id);
        InventoryItemCategoryResponse response = inventoryItemCategoryService.updateInventoryItemCategory(id, request);
        log.info("Inventory item category updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_CATEGORY_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<InventoryItemCategoryPageResponse> getInventoryItemCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get inventory item categories request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        InventoryItemCategoryPageResponse response = inventoryItemCategoryService.getInventoryItemCategoriesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} inventory item categories", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemCategoryResponse> getInventoryItemCategoryById(@PathVariable Long id) {
        log.info("Get inventory item category by ID request received for ID: {}", id);
        InventoryItemCategoryResponse response = inventoryItemCategoryService.getInventoryItemCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item category not found"));
        log.info("Inventory item category retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteInventoryItemCategory(@PathVariable Long id) {
        log.info("Delete inventory item category request received for ID: {}", id);
        inventoryItemCategoryService.deleteInventoryItemCategory(id);
        log.info("Inventory item category deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_CATEGORY_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
