package com.cafex.pos.controller;

import com.cafex.pos.dto.InventoryItemUnitPageResponse;
import com.cafex.pos.dto.InventoryItemUnitRequest;
import com.cafex.pos.dto.InventoryItemUnitResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.InventoryItemUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-item-units")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InventoryItemUnitController {

    private final InventoryItemUnitService inventoryItemUnitService;

    @PostMapping
    public ResponseEntity<OperationResponse> createInventoryItemUnit(@Valid @RequestBody InventoryItemUnitRequest request) {
        log.info("Create inventory item unit request received for key: {}", request.getKey());
        InventoryItemUnitResponse response = inventoryItemUnitService.createInventoryItemUnit(request);
        log.info("Inventory item unit created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_UNIT_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateInventoryItemUnit(@PathVariable Long id, @Valid @RequestBody InventoryItemUnitRequest request) {
        log.info("Update inventory item unit request received for ID: {}", id);
        InventoryItemUnitResponse response = inventoryItemUnitService.updateInventoryItemUnit(id, request);
        log.info("Inventory item unit updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_UNIT_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<InventoryItemUnitPageResponse> getInventoryItemUnits(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get inventory item units request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        InventoryItemUnitPageResponse response = inventoryItemUnitService.getInventoryItemUnitsWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} inventory item units", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemUnitResponse> getInventoryItemUnitById(@PathVariable Long id) {
        log.info("Get inventory item unit by ID request received for ID: {}", id);
        InventoryItemUnitResponse response = inventoryItemUnitService.getInventoryItemUnitById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item unit not found"));
        log.info("Inventory item unit retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteInventoryItemUnit(@PathVariable Long id) {
        log.info("Delete inventory item unit request received for ID: {}", id);
        inventoryItemUnitService.deleteInventoryItemUnit(id);
        log.info("Inventory item unit deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "INVENTORY_ITEM_UNIT_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
