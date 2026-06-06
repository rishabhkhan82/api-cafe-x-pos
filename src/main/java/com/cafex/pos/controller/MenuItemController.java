package com.cafex.pos.controller;

import com.cafex.pos.dto.MenuItemRequest;
import com.cafex.pos.dto.MenuItemResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.MenuItemPageResponse;
import com.cafex.pos.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-items")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveMenuItem(@Valid @RequestBody MenuItemRequest menuItemRequest) {
        log.info("Save menu item request received for itemId: {}", menuItemRequest.getItemId());
        try {
            MenuItemResponse response = menuItemService.saveMenuItem(menuItemRequest);
            log.info("Menu item saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEM_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save menu item: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "MENU_ITEM_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemRequest menuItemRequest) {
        log.info("Update menu item request received for ID: {}", id);
        try {
            MenuItemResponse response = menuItemService.updateMenuItem(id, menuItemRequest);
            log.info("Menu item updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEM_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update menu item: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "MENU_ITEM_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<MenuItemPageResponse> getMenuItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String isAvailable,
            @RequestParam(required = false) String isActive,
            @RequestParam(required = false) String isVegetarian,
            @RequestParam(name = "is_spicy", required = false) String isSpicy,
            @RequestParam(name = "is_featured", required = false) String isFeatured,
            @RequestParam(name = "is_popular", required = false) String isPopular,
            @RequestParam(name = "is_recommended", required = false) String isRecommended,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get menu items request received with filters - name: {}, category: {}, restaurantId: {}, isAvailable: {}, isActive: {}, isVegetarian: {}, isSpicy: {}, isPopular: {}, isFeatured: {}, isRecommended: {}, page: {}, size: {}",
                name, category, restaurantId, isAvailable, isActive, isVegetarian, isSpicy, isPopular, isFeatured, isRecommended, page, size);
        try {
            MenuItemPageResponse response = menuItemService.getMenuItemsWithFilters(name, category, restaurantId, isAvailable, isActive, isVegetarian, isSpicy, isPopular, isFeatured, isRecommended, page, size);
            log.info("Retrieved {} menu items (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get menu items: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(@PathVariable Long id) {
        log.info("Get menu item by ID request received for ID: {}", id);
        try {
            MenuItemResponse response = menuItemService.getMenuItemById(id)
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            log.info("Menu item retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get menu item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteMenuItem(@PathVariable Long id) {
        log.info("Delete menu item request received for ID: {}", id);
        try {
            menuItemService.deleteMenuItem(id);
            log.info("Menu item deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEM_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete menu item: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "MENU_ITEM_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}