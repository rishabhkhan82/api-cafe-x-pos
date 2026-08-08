package com.cafex.pos.controller;

import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.RestaurantMenuCategoryPageResponse;
import com.cafex.pos.dto.RestaurantMenuCategoryRequest;
import com.cafex.pos.dto.RestaurantMenuCategoryResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.RestaurantMenuCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/restaurant-menu-categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class RestaurantMenuCategoryController {

    private final RestaurantMenuCategoryService restaurantMenuCategoryService;

    @PostMapping
    public ResponseEntity<OperationResponse> createRestaurantMenuCategory(@Valid @RequestBody RestaurantMenuCategoryRequest request) {
        log.info("Create restaurant menu category request received for key: {} and restaurantId: {}", request.getKey(), request.getRestaurantId());
        RestaurantMenuCategoryResponse response = restaurantMenuCategoryService.createRestaurantMenuCategory(request);
        log.info("Restaurant menu category created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_MENU_CATEGORY_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{restaurantId}/{id}")
    public ResponseEntity<OperationResponse> updateRestaurantMenuCategory(@PathVariable Long restaurantId, @PathVariable Long id, @Valid @RequestBody RestaurantMenuCategoryRequest request) {
        log.info("Update restaurant menu category request received for ID: {} and restaurantId: {}", id, restaurantId);
        RestaurantMenuCategoryResponse response = restaurantMenuCategoryService.updateRestaurantMenuCategory(restaurantId, id, request);
        log.info("Restaurant menu category updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_MENU_CATEGORY_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<RestaurantMenuCategoryPageResponse> getRestaurantMenuCategories(
            @RequestParam Long restaurantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get restaurant menu categories request received for restaurantId: {} with filters - name: {}, isActive: {}, page: {}, size: {}",
                restaurantId, name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        RestaurantMenuCategoryPageResponse response = restaurantMenuCategoryService.getRestaurantMenuCategoriesWithFilters(restaurantId, name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} restaurant menu categories", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{restaurantId}/{id}")
    public ResponseEntity<RestaurantMenuCategoryResponse> getRestaurantMenuCategoryById(@PathVariable Long restaurantId, @PathVariable Long id) {
        log.info("Get restaurant menu category by ID request received for ID: {} and restaurantId: {}", id, restaurantId);
        RestaurantMenuCategoryResponse response = restaurantMenuCategoryService.getRestaurantMenuCategoryById(restaurantId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found"));
        log.info("Restaurant menu category retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/{restaurantId}")
    public ResponseEntity<List<RestaurantMenuCategoryResponse>> getActiveRestaurantMenuCategories(@PathVariable Long restaurantId) {
        log.info("Get active restaurant menu categories request received for restaurantId: {}", restaurantId);
        List<RestaurantMenuCategoryResponse> response = restaurantMenuCategoryService.getActiveRestaurantMenuCategories(restaurantId);
        log.info("Retrieved {} active restaurant menu categories", response.size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{restaurantId}/{id}")
    public ResponseEntity<OperationResponse> deleteRestaurantMenuCategory(@PathVariable Long restaurantId, @PathVariable Long id) {
        log.info("Delete restaurant menu category request received for ID: {} and restaurantId: {}", id, restaurantId);
        restaurantMenuCategoryService.deleteRestaurantMenuCategory(restaurantId, id);
        log.info("Restaurant menu category deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_MENU_CATEGORY_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
