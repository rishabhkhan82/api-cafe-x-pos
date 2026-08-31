package com.cafex.pos.controller;

import com.cafex.pos.dto.MenuItemAddonRequest;
import com.cafex.pos.dto.MenuItemAddonResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.MenuItemAddonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-item-addons")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuItemAddonController {

    private final MenuItemAddonService menuItemAddonService;

    @PostMapping("/menu-item/{menuItemId}/addons")
    public ResponseEntity<OperationResponse> linkAddon(@PathVariable Long menuItemId, @Valid @RequestBody MenuItemAddonRequest request) {
        log.info("Link add-on request received for addonId: {} to menu item {}", request.getAddonId(), menuItemId);
        MenuItemAddonResponse response = menuItemAddonService.linkAddonToMenuItem(menuItemId, request);
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEM_ADDON_LINKED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateMenuItemAddon(@PathVariable Long id, @Valid @RequestBody MenuItemAddonRequest request) {
        log.info("Update menu item add-on request received for ID: {}", id);
        MenuItemAddonResponse response = menuItemAddonService.updateMenuItemAddon(id, request);
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEM_ADDON_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<MenuItemAddonResponse>> getAddonsByMenuItem(@PathVariable Long menuItemId) {
        log.info("Get add-ons for menu item request received for menuItemId: {}", menuItemId);
        List<MenuItemAddonResponse> response = menuItemAddonService.getAddonsByMenuItemId(menuItemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> unlinkAddon(@PathVariable Long id) {
        log.info("Unlink add-on request received for ID: {}", id);
        menuItemAddonService.unlinkAddonFromMenuItem(id);
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEM_ADDON_UNLINKED", id, null);
        return ResponseEntity.ok(operationResponse);
    }

    @DeleteMapping("/menu-item/{menuItemId}")
    public ResponseEntity<OperationResponse> deleteByMenuItem(@PathVariable Long menuItemId) {
        log.info("Delete all add-on links for menu item request received for menuItemId: {}", menuItemId);
        menuItemAddonService.deleteByMenuItemId(menuItemId);
        OperationResponse operationResponse = new OperationResponse("success", "MENU_ITEM_ADDONS_DELETED", menuItemId, null);
        return ResponseEntity.ok(operationResponse);
    }
}
