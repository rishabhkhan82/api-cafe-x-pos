package com.cafex.pos.controller;

import com.cafex.pos.dto.MenuAddonRequest;
import com.cafex.pos.dto.MenuAddonResponse;
import com.cafex.pos.dto.MenuAddonPageResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.MenuAddonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-addons")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuAddonController {

    private final MenuAddonService menuAddonService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveMenuAddon(@Valid @RequestBody MenuAddonRequest menuAddonRequest) {
        log.info("Save menu add-on request received for name: {}", menuAddonRequest.getName());
        MenuAddonResponse response = menuAddonService.saveMenuAddon(menuAddonRequest);
        log.info("Menu add-on saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ADDON_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateMenuAddon(@PathVariable Long id, @Valid @RequestBody MenuAddonRequest menuAddonRequest) {
        log.info("Update menu add-on request received for ID: {}", id);
        MenuAddonResponse response = menuAddonService.updateMenuAddon(id, menuAddonRequest);
        log.info("Menu add-on updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ADDON_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<MenuAddonPageResponse> getMenuAddons(
            @RequestParam(required = false) String name,
            @RequestParam("restaurant_id") String restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get menu add-ons request received with filters - name: {}, restaurantId: {}, page: {}, size: {}", name, restaurantId, page, size);
        MenuAddonPageResponse response = menuAddonService.getMenuAddonsWithFilters(name, restaurantId, page, size);
        log.info("Retrieved {} menu add-ons (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuAddonResponse> getMenuAddonById(@PathVariable Long id) {
        log.info("Get menu add-on by ID request received for ID: {}", id);
        MenuAddonResponse response = menuAddonService.getMenuAddonById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu add-on not found"));
        log.info("Menu add-on retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteMenuAddon(@PathVariable Long id) {
        log.info("Delete menu add-on request received for ID: {}", id);
        menuAddonService.deleteMenuAddon(id);
        log.info("Menu add-on deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ADDON_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
