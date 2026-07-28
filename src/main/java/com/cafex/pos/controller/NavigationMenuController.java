package com.cafex.pos.controller;

import com.cafex.pos.dto.NavigationMenuRequest;
import com.cafex.pos.dto.NavigationMenuResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.NavigationMenuPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.NavigationMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/navigation-menus")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class NavigationMenuController {

    private final NavigationMenuService navigationMenuService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveNavigationMenu(@Valid @RequestBody NavigationMenuRequest navigationMenuRequest) {
        log.info("Save navigation menu request received for menuId: {}", navigationMenuRequest.getMenuId());
        NavigationMenuResponse response = navigationMenuService.saveNavigationMenu(navigationMenuRequest);
        log.info("Navigation menu saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "NAVIGATION_MENU_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateNavigationMenu(@PathVariable Long id, @Valid @RequestBody NavigationMenuRequest navigationMenuRequest) {
        log.info("Update navigation menu request received for ID: {}", id);
        NavigationMenuResponse response = navigationMenuService.updateNavigationMenu(id, navigationMenuRequest);
        log.info("Navigation menu updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "NAVIGATION_MENU_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<NavigationMenuPageResponse> getNavigationMenus(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get navigation menus request received with filters - name: {}, role: {}, isActive: {}, type: {}, page: {}, size: {}",
                name, role, isActive, type, page, size);
        if ((name == null || name.isEmpty()) &&
            (role == null || role.isEmpty()) &&
            isActive == null &&
            (type == null || type.isEmpty()) &&
            page == null &&
            size == null) {
            List<NavigationMenuResponse> allMenus = navigationMenuService.getAllNavigationMenus();
            log.info("Retrieved all {} navigation menus", allMenus.size());
            NavigationMenuPageResponse response = new NavigationMenuPageResponse(
                allMenus,
                0,
                0,
                (long) allMenus.size()
            );
            return ResponseEntity.ok(response);
        }
        int pageValue = (page != null) ? Math.max(0, page - 1) : 0;
        int sizeValue = (size != null) ? size : 10;
        NavigationMenuPageResponse response = navigationMenuService.getNavigationMenusWithFilters(name, role, isActive, type, pageValue, sizeValue);
        log.info("Retrieved {} navigation menus (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NavigationMenuResponse> getNavigationMenuById(@PathVariable Long id) {
        log.info("Get navigation menu by ID request received for ID: {}", id);
        NavigationMenuResponse response = navigationMenuService.getNavigationMenuById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Navigation menu not found"));
        log.info("Navigation menu retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteNavigationMenu(@PathVariable Long id) {
        log.info("Delete navigation menu request received for ID: {}", id);
        navigationMenuService.deleteNavigationMenu(id);
        log.info("Navigation menu deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "NAVIGATION_MENU_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}