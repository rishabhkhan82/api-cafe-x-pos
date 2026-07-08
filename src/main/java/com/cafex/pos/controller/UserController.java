package com.cafex.pos.controller;

import com.cafex.pos.dto.UserRequest;
import com.cafex.pos.dto.UserResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.UserPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Save user request received for username: {}", userRequest.getUsername());
        UserResponse response = userService.saveUser(userRequest);
        log.info("User saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "USER_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("Update user request received for ID: {}", id);
        UserResponse response = userService.updateUser(id, userRequest);
        log.info("User updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "USER_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<UserPageResponse> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get users request received with filters - name: {}, restaurantId: {}, role: {}, status: {}, userType: {}, page: {}, size: {}",
                name, restaurantId, role, status, userType, page, size);
        UserPageResponse response = userService.getUsersWithFilters(name, restaurantId, role, status, userType, page, size);
        log.info("Retrieved {} users (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Get user by ID request received for ID: {}", id);
        UserResponse response = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        log.info("User retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteUser(@PathVariable Long id) {
        log.info("Delete user request received for ID: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "USER_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}