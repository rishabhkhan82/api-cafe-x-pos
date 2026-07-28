package com.cafex.pos.controller;

import com.cafex.pos.dto.InventoryItemTypePageResponse;
import com.cafex.pos.dto.InventoryItemTypeRequest;
import com.cafex.pos.dto.InventoryItemTypeResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.InventoryItemTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-item-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InventoryItemTypeController {

    private final InventoryItemTypeService inventoryItemTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createInventoryItemType(@Valid @RequestBody InventoryItemTypeRequest request) {
        log.info("Create inventory item type request received for key: {}", request.getKey());
        InventoryItemTypeResponse response = inventoryItemTypeService.createInventoryItemType(request);
        log.info("Inventory item type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateInventoryItemType(@PathVariable Long id, @Valid @RequestBody InventoryItemTypeRequest request) {
        log.info("Update inventory item type request received for ID: {}", id);
        InventoryItemTypeResponse response = inventoryItemTypeService.updateInventoryItemType(id, request);
        log.info("Inventory item type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<InventoryItemTypePageResponse> getInventoryItemTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get inventory item types request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        InventoryItemTypePageResponse response = inventoryItemTypeService.getInventoryItemTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} inventory item types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemTypeResponse> getInventoryItemTypeById(@PathVariable Long id) {
        log.info("Get inventory item type by ID request received for ID: {}", id);
        InventoryItemTypeResponse response = inventoryItemTypeService.getInventoryItemTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item type not found"));
        log.info("Inventory item type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteInventoryItemType(@PathVariable Long id) {
        log.info("Delete inventory item type request received for ID: {}", id);
        inventoryItemTypeService.deleteInventoryItemType(id);
        log.info("Inventory item type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
