package com.cafex.pos.controller;

import com.cafex.pos.dto.UserRolesRequest;
import com.cafex.pos.dto.UserRolesResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.UserRolesPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.UserRolesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-roles")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserRolesController {

    private final UserRolesService userRolesService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveUserRole(@Valid @RequestBody UserRolesRequest userRolesRequest) {
        log.info("Save user role request received for roleId: {}", userRolesRequest.getRoleId());
        UserRolesResponse response = userRolesService.saveUserRole(userRolesRequest);
        log.info("User role saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "USER_ROLE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRolesRequest userRolesRequest) {
        log.info("Update user role request received for ID: {}", id);
        UserRolesResponse response = userRolesService.updateUserRole(id, userRolesRequest);
        log.info("User role updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "USER_ROLE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<UserRolesPageResponse> getUserRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get user roles request received with filters - name: {}, code: {}, isActive: {}, page: {}, size: {}",
                name, code, isActive, page, size);
        UserRolesPageResponse response = userRolesService.getUserRolesWithFilters(name, code, isActive, page, size);
        log.info("Retrieved {} user roles (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRolesResponse> getUserRoleById(@PathVariable Long id) {
        log.info("Get user role by ID request received for ID: {}", id);
        UserRolesResponse response = userRolesService.getUserRoleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));
        log.info("User role retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteUserRole(@PathVariable Long id) {
        log.info("Delete user role request received for ID: {}", id);
        userRolesService.deleteUserRole(id);
        log.info("User role deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "USER_ROLE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}