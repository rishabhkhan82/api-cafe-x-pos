package com.cafex.pos.controller;

import com.cafex.pos.dto.InventoryItemRequest;
import com.cafex.pos.dto.InventoryItemResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.InventoryItemPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.InventoryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-items")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveInventoryItem(@Valid @RequestBody InventoryItemRequest inventoryItemRequest) {
        log.info("Save inventory item request received for itemId: {}", inventoryItemRequest.getItemId());
        InventoryItemResponse response = inventoryItemService.saveInventoryItem(inventoryItemRequest);
        log.info("Inventory item saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateInventoryItem(@PathVariable Long id, @Valid @RequestBody InventoryItemRequest inventoryItemRequest) {
        log.info("Update inventory item request received for ID: {}", id);
        InventoryItemResponse response = inventoryItemService.updateInventoryItem(id, inventoryItemRequest);
        log.info("Inventory item updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<InventoryItemPageResponse> getInventoryItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String unitOfMeasure,
            @RequestParam("restaurant_id") String restaurantId,
            @RequestParam(required = false) String isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get inventory items request received with filters - name: {}, category: {}, unitOfMeasure: {}, restaurantId: {}, isActive: {}, page: {}, size: {}",
                name, category, unitOfMeasure, restaurantId, isActive, page, size);
        InventoryItemPageResponse response = inventoryItemService.getInventoryItemsWithFilters(name, category, unitOfMeasure, restaurantId, isActive, page, size);
        log.info("Retrieved {} inventory items (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> getInventoryItemById(@PathVariable Long id) {
        log.info("Get inventory item by ID request received for ID: {}", id);
        InventoryItemResponse response = inventoryItemService.getInventoryItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));
        log.info("Inventory item retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteInventoryItem(@PathVariable Long id) {
        log.info("Delete inventory item request received for ID: {}", id);
        inventoryItemService.deleteInventoryItem(id);
        log.info("Inventory item deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
