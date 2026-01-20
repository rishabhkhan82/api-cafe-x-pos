package com.cafex.pos.controller;

import com.cafex.pos.dto.SubscriptionPlansRequest;
import com.cafex.pos.dto.SubscriptionPlansResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.SubscriptionPlansPageResponse;
import com.cafex.pos.service.SubscriptionPlansService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription-plans")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SubscriptionPlansController {

    private final SubscriptionPlansService subscriptionPlansService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveSubscriptionPlan(@Valid @RequestBody SubscriptionPlansRequest subscriptionPlansRequest) {
        log.info("Save subscription plan request received for planId: {}", subscriptionPlansRequest.getPlan_id());
        try {
            SubscriptionPlansResponse response = subscriptionPlansService.saveSubscriptionPlan(subscriptionPlansRequest);
            log.info("Subscription plan saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "SUBSCRIPTION_PLAN_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save subscription plan: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "SUBSCRIPTION_PLAN_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateSubscriptionPlan(@PathVariable Long id, @Valid @RequestBody SubscriptionPlansRequest subscriptionPlansRequest) {
        log.info("Update subscription plan request received for ID: {}", id);
        try {
            SubscriptionPlansResponse response = subscriptionPlansService.updateSubscriptionPlan(id, subscriptionPlansRequest);
            log.info("Subscription plan updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "SUBSCRIPTION_PLAN_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update subscription plan: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "SUBSCRIPTION_PLAN_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<SubscriptionPlansPageResponse> getSubscriptionPlans(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String billingCycle,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isPopular,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get subscription plans request received with filters - name: {}, billingCycle: {}, isActive: {}, isPopular: {}, page: {}, size: {}",
                name, billingCycle, isActive, isPopular, page, size);
        try {
            // If no filters and no pagination parameters, return all records
            if ((name == null || name.isEmpty()) &&
                (billingCycle == null || billingCycle.isEmpty()) &&
                isActive == null &&
                isPopular == null &&
                page == null &&
                size == null) {
                List<SubscriptionPlansResponse> allPlans = subscriptionPlansService.getAllSubscriptionPlans();
                log.info("Retrieved all {} subscription plans", allPlans.size());
                SubscriptionPlansPageResponse response = new SubscriptionPlansPageResponse(
                    allPlans,
                    0,
                    0,
                    (long) allPlans.size()
                );
                return ResponseEntity.ok(response);
            }
            // Otherwise, use pagination
            int pageValue = (page != null) ? Math.max(0, page - 1) : 0;
            int sizeValue = (size != null) ? size : 10;
            SubscriptionPlansPageResponse response = subscriptionPlansService.getSubscriptionPlansWithFilters(name, billingCycle, isActive, isPopular, pageValue, sizeValue);
            log.info("Retrieved {} subscription plans (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get subscription plans: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlansResponse> getSubscriptionPlanById(@PathVariable Long id) {
        log.info("Get subscription plan by ID request received for ID: {}", id);
        try {
            SubscriptionPlansResponse response = subscriptionPlansService.getSubscriptionPlanById(id)
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
            log.info("Subscription plan retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get subscription plan: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteSubscriptionPlan(@PathVariable Long id) {
        log.info("Delete subscription plan request received for ID: {}", id);
        try {
            subscriptionPlansService.deleteSubscriptionPlan(id);
            log.info("Subscription plan deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "SUBSCRIPTION_PLAN_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete subscription plan: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "SUBSCRIPTION_PLAN_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}