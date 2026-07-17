package com.cafex.pos.controller;

import com.cafex.pos.dto.MenuItemsTypePageResponse;
import com.cafex.pos.dto.MenuItemsTypeRequest;
import com.cafex.pos.dto.MenuItemsTypeResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.MenuItemsTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu-items-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuItemsTypeController {

    private final MenuItemsTypeService menuItemsTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createMenuItemsType(@Valid @RequestBody MenuItemsTypeRequest request) {
        log.info("Create menu items type request received for key: {}", request.getKey());
        MenuItemsTypeResponse response = menuItemsTypeService.createMenuItemsType(request);
        log.info("Menu items type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEMS_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateMenuItemsType(@PathVariable Long id, @Valid @RequestBody MenuItemsTypeRequest request) {
        log.info("Update menu items type request received for ID: {}", id);
        MenuItemsTypeResponse response = menuItemsTypeService.updateMenuItemsType(id, request);
        log.info("Menu items type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEMS_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<MenuItemsTypePageResponse> getMenuItemsTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get menu items types request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        MenuItemsTypePageResponse response = menuItemsTypeService.getMenuItemsTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} menu items types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemsTypeResponse> getMenuItemsTypeById(@PathVariable Long id) {
        log.info("Get menu items type by ID request received for ID: {}", id);
        MenuItemsTypeResponse response = menuItemsTypeService.getMenuItemsTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu items type not found"));
        log.info("Menu items type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteMenuItemsType(@PathVariable Long id) {
        log.info("Delete menu items type request received for ID: {}", id);
        menuItemsTypeService.deleteMenuItemsType(id);
        log.info("Menu items type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEMS_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
