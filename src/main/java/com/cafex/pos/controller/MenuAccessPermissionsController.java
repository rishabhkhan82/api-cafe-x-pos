package com.cafex.pos.controller;

import com.cafex.pos.dto.MenuAccessPermissionsRequest;
import com.cafex.pos.dto.MenuAccessPermissionsResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.MenuAccessPermissionsPageResponse;
import com.cafex.pos.service.MenuAccessPermissionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-access-permissions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuAccessPermissionsController {

    private final MenuAccessPermissionsService menuAccessPermissionsService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveMenuAccessPermission(@Valid @RequestBody MenuAccessPermissionsRequest request) {
        log.info("Save menu access permission request received for permissionId: {}", request.getPermissionId());
        try {
            MenuAccessPermissionsResponse response = menuAccessPermissionsService.saveMenuAccessPermission(request);
            log.info("Menu access permission saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "MENU_ACCESS_PERMISSION_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save menu access permission: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "MENU_ACCESS_PERMISSION_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateMenuAccessPermission(@PathVariable Long id, @Valid @RequestBody MenuAccessPermissionsRequest request) {
        log.info("Update menu access permission request received for ID: {}", id);
        try {
            MenuAccessPermissionsResponse response = menuAccessPermissionsService.updateMenuAccessPermission(id, request);
            log.info("Menu access permission updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "MENU_ACCESS_PERMISSION_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update menu access permission: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "MENU_ACCESS_PERMISSION_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<MenuAccessPermissionsPageResponse> getMenuAccessPermissions(
            @RequestParam(required = false) String permissionId,
            @RequestParam(required = false) String menuId,
            @RequestParam(required = false) String roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get menu access permissions request received with filters - permissionId: {}, menuId: {}, roleId: {}, page: {}, size: {}",
                permissionId, menuId, roleId, page, size);
        try {
            MenuAccessPermissionsPageResponse response = menuAccessPermissionsService.getMenuAccessPermissionsWithFilters(permissionId, menuId, roleId, page, size);
            log.info("Retrieved {} menu access permissions (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get menu access permissions: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<List<MenuAccessPermissionsResponse>> getMenuAccessPermissionsByRoleId(@PathVariable Long id) {
        log.info("Get menu access permissions by role ID request received for role ID: {}", id);
        try {
            List<MenuAccessPermissionsResponse> response = menuAccessPermissionsService.getMenuAccessPermissionsByRoleId(id);
            log.info("Retrieved {} menu access permissions for role ID: {}", response.size(), id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get menu access permissions by role ID: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteMenuAccessPermission(@PathVariable Long id) {
        log.info("Delete menu access permission request received for ID: {}", id);
        try {
            menuAccessPermissionsService.deleteMenuAccessPermission(id);
            log.info("Menu access permission deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "MENU_ACCESS_PERMISSION_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete menu access permission: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "MENU_ACCESS_PERMISSION_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}