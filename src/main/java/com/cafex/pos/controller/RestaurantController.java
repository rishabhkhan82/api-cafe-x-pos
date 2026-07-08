package com.cafex.pos.controller;

import com.cafex.pos.dto.RestaurantRequest;
import com.cafex.pos.dto.RestaurantResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.RestaurantPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest) {
        log.info("Save restaurant request received for name: {}", restaurantRequest.getName());
        RestaurantResponse response = restaurantService.saveRestaurant(restaurantRequest);
        log.info("Restaurant saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantRequest restaurantRequest) {
        log.info("Update restaurant request received for ID: {}", id);
        RestaurantResponse response = restaurantService.updateRestaurant(id, restaurantRequest);
        log.info("Restaurant updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<RestaurantPageResponse> getRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ownerName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get restaurants request received with filters - name: {}, status: {}, ownerName: {}, page: {}, size: {}",
                name, status, ownerName, page, size);
        RestaurantPageResponse response = restaurantService.getRestaurantsWithFilters(name, status, ownerName, page, size);
        log.info("Retrieved {} restaurants (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        log.info("Get restaurant by ID request received for ID: {}", id);
        RestaurantResponse response = restaurantService.getRestaurantById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        log.info("Restaurant retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteRestaurant(@PathVariable Long id) {
        log.info("Delete restaurant request received for ID: {}", id);
        restaurantService.deleteRestaurant(id);
        log.info("Restaurant deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}