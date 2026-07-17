package com.cafex.pos.controller;

import com.cafex.pos.dto.NavigationMenuTypePageResponse;
import com.cafex.pos.dto.NavigationMenuTypeRequest;
import com.cafex.pos.dto.NavigationMenuTypeResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.NavigationMenuTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/navigation-menu-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class NavigationMenuTypeController {

    private final NavigationMenuTypeService navigationMenuTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createNavigationMenuType(@Valid @RequestBody NavigationMenuTypeRequest request) {
        log.info("Create navigation menu type request received for key: {}", request.getKey());
        NavigationMenuTypeResponse response = navigationMenuTypeService.createNavigationMenuType(request);
        log.info("Navigation menu type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "NAVIGATION_MENU_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateNavigationMenuType(@PathVariable Long id, @Valid @RequestBody NavigationMenuTypeRequest request) {
        log.info("Update navigation menu type request received for ID: {}", id);
        NavigationMenuTypeResponse response = navigationMenuTypeService.updateNavigationMenuType(id, request);
        log.info("Navigation menu type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "NAVIGATION_MENU_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<NavigationMenuTypePageResponse> getNavigationMenuTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get navigation menu types request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        NavigationMenuTypePageResponse response = navigationMenuTypeService.getNavigationMenuTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} navigation menu types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NavigationMenuTypeResponse> getNavigationMenuTypeById(@PathVariable Long id) {
        log.info("Get navigation menu type by ID request received for ID: {}", id);
        NavigationMenuTypeResponse response = navigationMenuTypeService.getNavigationMenuTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Navigation menu type not found"));
        log.info("Navigation menu type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteNavigationMenuType(@PathVariable Long id) {
        log.info("Delete navigation menu type request received for ID: {}", id);
        navigationMenuTypeService.deleteNavigationMenuType(id);
        log.info("Navigation menu type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "NAVIGATION_MENU_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
