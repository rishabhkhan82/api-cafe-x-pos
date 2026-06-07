package com.cafex.pos.controller;

import com.cafex.pos.dto.LoyaltyTransactionsPageResponse;
import com.cafex.pos.dto.LoyaltyTransactionsRequest;
import com.cafex.pos.dto.LoyaltyTransactionsResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.service.LoyaltyTransactionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty-transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class LoyaltyTransactionsController {

    private final LoyaltyTransactionsService loyaltyTransactionsService;

    @PostMapping
    public ResponseEntity<OperationResponse> createTransaction(@Valid @RequestBody LoyaltyTransactionsRequest request) {
        log.info("Create loyalty transaction request received for transactionId: {}", request.getTransactionId());
        try {
            LoyaltyTransactionsResponse response = loyaltyTransactionsService.createTransaction(request);
            log.info("Loyalty transaction created successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "LOYALTY_TRANSACTION_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to create loyalty transaction: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "LOYALTY_TRANSACTION_CREATE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateTransaction(@PathVariable Long id, @Valid @RequestBody LoyaltyTransactionsRequest request) {
        log.info("Update loyalty transaction request received for ID: {}", id);
        try {
            LoyaltyTransactionsResponse response = loyaltyTransactionsService.updateTransaction(id, request);
            log.info("Loyalty transaction updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "LOYALTY_TRANSACTION_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update loyalty transaction: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "LOYALTY_TRANSACTION_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<LoyaltyTransactionsPageResponse> getTransactions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String transactionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get loyalty transactions request received with filters - customerId: {}, restaurantId: {}, transactionType: {}, page: {}, size: {}",
                customerId, restaurantId, transactionType, page, size);
        try {
            LoyaltyTransactionsPageResponse response = loyaltyTransactionsService.getTransactionsWithFilters(
                    customerId, restaurantId, transactionType, page, size);
            log.info("Retrieved {} transactions (page {} of {})",
                    response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get loyalty transactions: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoyaltyTransactionsResponse> getTransactionById(@PathVariable Long id) {
        log.info("Get loyalty transaction by ID request received for ID: {}", id);
        try {
            LoyaltyTransactionsResponse response = loyaltyTransactionsService.getTransactionById(id)
                    .orElseThrow(() -> new RuntimeException("Loyalty transaction not found"));
            log.info("Loyalty transaction retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get loyalty transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteTransaction(@PathVariable Long id) {
        log.info("Delete loyalty transaction request received for ID: {}", id);
        try {
            loyaltyTransactionsService.deleteTransaction(id);
            log.info("Loyalty transaction deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "LOYALTY_TRANSACTION_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete loyalty transaction: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "LOYALTY_TRANSACTION_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<LoyaltyTransactionsPageResponse> getTransactionsByCustomer(@PathVariable Long customerId) {
        log.info("Get loyalty transactions by customer request received for customerId: {}", customerId);
        try {
            LoyaltyTransactionsPageResponse response = loyaltyTransactionsService.getTransactionsWithFilters(
                    String.valueOf(customerId), null, null, 1, 100);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get loyalty transactions by customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/type/{transactionType}")
    public ResponseEntity<LoyaltyTransactionsPageResponse> getTransactionsByType(@PathVariable String transactionType) {
        log.info("Get loyalty transactions by type request received for transactionType: {}", transactionType);
        try {
            LoyaltyTransactionsPageResponse response = loyaltyTransactionsService.getTransactionsWithFilters(
                    null, null, transactionType, 1, 100);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get loyalty transactions by type: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
