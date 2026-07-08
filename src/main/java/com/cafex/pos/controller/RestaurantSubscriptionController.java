package com.cafex.pos.controller;

import com.cafex.pos.dto.RestaurantSubscriptionRequest;
import com.cafex.pos.dto.RestaurantSubscriptionResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.RestaurantSubscriptionPageResponse;
import com.cafex.pos.dto.TrialSubscriptionRequest;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.RestaurantSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant-subscriptions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class RestaurantSubscriptionController {

    private final RestaurantSubscriptionService restaurantSubscriptionService;

    @PostMapping("/trial")
    public ResponseEntity<OperationResponse> createTrialSubscription(@Valid @RequestBody TrialSubscriptionRequest request) {
        log.info("Create trial subscription request received for restaurantId: {}, planId: {}, userId: {}", request.getRestaurantId(), request.getPlanId(), request.getUserId());
        RestaurantSubscriptionResponse response = restaurantSubscriptionService.createTrialSubscription(request.getRestaurantId(), request.getPlanId(), request.getUserId());
        log.info("Trial subscription created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "TRIAL_SUBSCRIPTION_CREATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping("/trial/check/{restaurantId}")
    public ResponseEntity<Boolean> checkTrialEligibility(@PathVariable Long restaurantId) {
        log.info("Check trial eligibility request received for restaurantId: {}", restaurantId);
        boolean hasUsedTrial = restaurantSubscriptionService.hasRestaurantUsedTrial(restaurantId);
        log.info("Trial eligibility check for restaurant {}: eligible={}", restaurantId, !hasUsedTrial);
        return ResponseEntity.ok(!hasUsedTrial); // Return true if eligible (not used trial)
    }

    @GetMapping("/active/{restaurantId}")
    public ResponseEntity<List<RestaurantSubscriptionResponse>> getActiveSubscriptions(@PathVariable Long restaurantId) {
        log.info("Get active subscriptions request received for restaurantId: {}", restaurantId);
        List<RestaurantSubscriptionResponse> response = restaurantSubscriptionService.getActiveSubscriptions(restaurantId);
        log.info("Retrieved {} active subscriptions for restaurant {}", response.size(), restaurantId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OperationResponse> saveRestaurantSubscription(@Valid @RequestBody RestaurantSubscriptionRequest restaurantSubscriptionRequest) {
        log.info("Save restaurant subscription request received for subscriptionId: {}", restaurantSubscriptionRequest.getSubscriptionId());
        RestaurantSubscriptionResponse response = restaurantSubscriptionService.saveRestaurantSubscription(restaurantSubscriptionRequest);
        log.info("Restaurant subscription saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_SUBSCRIPTION_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateRestaurantSubscription(@PathVariable Long id, @Valid @RequestBody RestaurantSubscriptionRequest restaurantSubscriptionRequest) {
        log.info("Update restaurant subscription request received for ID: {}", id);
        RestaurantSubscriptionResponse response = restaurantSubscriptionService.updateRestaurantSubscription(id, restaurantSubscriptionRequest);
        log.info("Restaurant subscription updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_SUBSCRIPTION_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<RestaurantSubscriptionPageResponse> getRestaurantSubscriptions(
            @RequestParam(required = false) String subscriptionId,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String planId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String autoRenew,
            @RequestParam(required = false) String cancelAtPeriodEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get restaurant subscriptions request received with filters - subscriptionId: {}, restaurantId: {}, planId: {}, status: {}, autoRenew: {}, cancelAtPeriodEnd: {}, page: {}, size: {}",
                subscriptionId, restaurantId, planId, status, autoRenew, cancelAtPeriodEnd, page, size);
        RestaurantSubscriptionPageResponse response = restaurantSubscriptionService.getRestaurantSubscriptionsWithFilters(subscriptionId, restaurantId, planId, status, autoRenew, cancelAtPeriodEnd, page, size);
        log.info("Retrieved {} restaurant subscriptions (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantSubscriptionResponse> getRestaurantSubscriptionById(@PathVariable Long id) {
        log.info("Get restaurant subscription by ID request received for ID: {}", id);
        RestaurantSubscriptionResponse response = restaurantSubscriptionService.getRestaurantSubscriptionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant subscription not found"));
        log.info("Restaurant subscription retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteRestaurantSubscription(@PathVariable Long id) {
        log.info("Delete restaurant subscription request received for ID: {}", id);
        restaurantSubscriptionService.deleteRestaurantSubscription(id);
        log.info("Restaurant subscription deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "RESTAURANT_SUBSCRIPTION_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}