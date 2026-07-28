package com.cafex.pos.controller;

import com.cafex.pos.dto.RestaurantStatusPageResponse;
import com.cafex.pos.dto.RestaurantStatusRequest;
import com.cafex.pos.dto.RestaurantStatusResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.RestaurantStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant-statuses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class RestaurantStatusController {

    private final RestaurantStatusService restaurantStatusService;

    @PostMapping
    public ResponseEntity<OperationResponse> createRestaurantStatus(@Valid @RequestBody RestaurantStatusRequest request) {
        log.info("Create restaurant status request received for key: {}", request.getKey());
        RestaurantStatusResponse response = restaurantStatusService.createRestaurantStatus(request);
        log.info("Restaurant status created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_STATUS_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateRestaurantStatus(@PathVariable Long id, @Valid @RequestBody RestaurantStatusRequest request) {
        log.info("Update restaurant status request received for ID: {}", id);
        RestaurantStatusResponse response = restaurantStatusService.updateRestaurantStatus(id, request);
        log.info("Restaurant status updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_STATUS_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<RestaurantStatusPageResponse> getRestaurantStatuses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get restaurant statuses request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        RestaurantStatusPageResponse response = restaurantStatusService.getRestaurantStatusesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} restaurant statuses", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantStatusResponse> getRestaurantStatusById(@PathVariable Long id) {
        log.info("Get restaurant status by ID request received for ID: {}", id);
        RestaurantStatusResponse response = restaurantStatusService.getRestaurantStatusById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant status not found"));
        log.info("Restaurant status retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteRestaurantStatus(@PathVariable Long id) {
        log.info("Delete restaurant status request received for ID: {}", id);
        restaurantStatusService.deleteRestaurantStatus(id);
        log.info("Restaurant status deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_STATUS_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
