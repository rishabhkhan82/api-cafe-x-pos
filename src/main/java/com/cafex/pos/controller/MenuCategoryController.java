package com.cafex.pos.controller;

import com.cafex.pos.dto.MenuCategoryPageResponse;
import com.cafex.pos.dto.MenuCategoryRequest;
import com.cafex.pos.dto.MenuCategoryResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.MenuCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu-categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @PostMapping
    public ResponseEntity<OperationResponse> createMenuCategory(@Valid @RequestBody MenuCategoryRequest request) {
        log.info("Create menu category request received for key: {}", request.getKey());
        MenuCategoryResponse response = menuCategoryService.createMenuCategory(request);
        log.info("Menu category created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "MENU_CATEGORY_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateMenuCategory(@PathVariable Long id, @Valid @RequestBody MenuCategoryRequest request) {
        log.info("Update menu category request received for ID: {}", id);
        MenuCategoryResponse response = menuCategoryService.updateMenuCategory(id, request);
        log.info("Menu category updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "MENU_CATEGORY_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<MenuCategoryPageResponse> getMenuCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get menu categories request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        MenuCategoryPageResponse response = menuCategoryService.getMenuCategoriesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} menu categories", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuCategoryResponse> getMenuCategoryById(@PathVariable Long id) {
        log.info("Get menu category by ID request received for ID: {}", id);
        MenuCategoryResponse response = menuCategoryService.getMenuCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found"));
        log.info("Menu category retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteMenuCategory(@PathVariable Long id) {
        log.info("Delete menu category request received for ID: {}", id);
        menuCategoryService.deleteMenuCategory(id);
        log.info("Menu category deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "MENU_CATEGORY_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
