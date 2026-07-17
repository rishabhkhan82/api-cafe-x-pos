package com.cafex.pos.controller;

import com.cafex.pos.dto.UserTypePageResponse;
import com.cafex.pos.dto.UserTypeRequest;
import com.cafex.pos.dto.UserTypeResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.UserTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserTypeController {

    private final UserTypeService userTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createUserType(@Valid @RequestBody UserTypeRequest request) {
        log.info("Create user type request received for key: {}", request.getKey());
        UserTypeResponse response = userTypeService.createUserType(request);
        log.info("User type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "USER_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateUserType(@PathVariable Long id, @Valid @RequestBody UserTypeRequest request) {
        log.info("Update user type request received for ID: {}", id);
        UserTypeResponse response = userTypeService.updateUserType(id, request);
        log.info("User type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "USER_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<UserTypePageResponse> getUserTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get user types request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        UserTypePageResponse response = userTypeService.getUserTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} user types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTypeResponse> getUserTypeById(@PathVariable Long id) {
        log.info("Get user type by ID request received for ID: {}", id);
        UserTypeResponse response = userTypeService.getUserTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User type not found"));
        log.info("User type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteUserType(@PathVariable Long id) {
        log.info("Delete user type request received for ID: {}", id);
        userTypeService.deleteUserType(id);
        log.info("User type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "USER_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
