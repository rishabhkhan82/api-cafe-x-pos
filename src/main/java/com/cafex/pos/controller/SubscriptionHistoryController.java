package com.cafex.pos.controller;

import com.cafex.pos.dto.SubscriptionHistoryRequest;
import com.cafex.pos.dto.SubscriptionHistoryResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.SubscriptionHistoryPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.SubscriptionHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription-histories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SubscriptionHistoryController {

    private final SubscriptionHistoryService subscriptionHistoryService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveSubscriptionHistory(@Valid @RequestBody SubscriptionHistoryRequest subscriptionHistoryRequest) {
        log.info("Save subscription history request received for historyId: {}", subscriptionHistoryRequest.getHistoryId());
        SubscriptionHistoryResponse response = subscriptionHistoryService.saveSubscriptionHistory(subscriptionHistoryRequest);
        log.info("Subscription history saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "SUBSCRIPTION_HISTORY_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateSubscriptionHistory(@PathVariable Long id, @Valid @RequestBody SubscriptionHistoryRequest subscriptionHistoryRequest) {
        log.info("Update subscription history request received for ID: {}", id);
        SubscriptionHistoryResponse response = subscriptionHistoryService.updateSubscriptionHistory(id, subscriptionHistoryRequest);
        log.info("Subscription history updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "SUBSCRIPTION_HISTORY_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<SubscriptionHistoryPageResponse> getSubscriptionHistories(
            @RequestParam(required = false) String historyId,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) String initiatedBy,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get subscription histories request received with filters - historyId: {}, restaurantId: {}, changeType: {}, initiatedBy: {}, paymentStatus: {}, page: {}, size: {}",
                historyId, restaurantId, changeType, initiatedBy, paymentStatus, page, size);
        SubscriptionHistoryPageResponse response = subscriptionHistoryService.getSubscriptionHistoriesWithFilters(historyId, restaurantId, changeType, initiatedBy, paymentStatus, page, size);
        log.info("Retrieved {} subscription histories (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionHistoryResponse> getSubscriptionHistoryById(@PathVariable Long id) {
        log.info("Get subscription history by ID request received for ID: {}", id);
        SubscriptionHistoryResponse response = subscriptionHistoryService.getSubscriptionHistoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription history not found"));
        log.info("Subscription history retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteSubscriptionHistory(@PathVariable Long id) {
        log.info("Delete subscription history request received for ID: {}", id);
        subscriptionHistoryService.deleteSubscriptionHistory(id);
        log.info("Subscription history deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "SUBSCRIPTION_HISTORY_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}