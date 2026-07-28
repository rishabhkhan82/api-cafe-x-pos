package com.cafex.pos.controller;

import com.cafex.pos.dto.BillingCyclePageResponse;
import com.cafex.pos.dto.BillingCycleRequest;
import com.cafex.pos.dto.BillingCycleResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.BillingCycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing-cycles")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class BillingCycleController {

    private final BillingCycleService billingCycleService;

    @PostMapping
    public ResponseEntity<OperationResponse> createBillingCycle(@Valid @RequestBody BillingCycleRequest request) {
        log.info("Create billing cycle request received for key: {}", request.getKey());
        BillingCycleResponse response = billingCycleService.createBillingCycle(request);
        log.info("Billing cycle created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "BILLING_CYCLE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateBillingCycle(@PathVariable Long id, @Valid @RequestBody BillingCycleRequest request) {
        log.info("Update billing cycle request received for ID: {}", id);
        BillingCycleResponse response = billingCycleService.updateBillingCycle(id, request);
        log.info("Billing cycle updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "BILLING_CYCLE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<BillingCyclePageResponse> getBillingCycles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get billing cycles request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        BillingCyclePageResponse response = billingCycleService.getBillingCyclesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} billing cycles", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingCycleResponse> getBillingCycleById(@PathVariable Long id) {
        log.info("Get billing cycle by ID request received for ID: {}", id);
        BillingCycleResponse response = billingCycleService.getBillingCycleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing cycle not found"));
        log.info("Billing cycle retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteBillingCycle(@PathVariable Long id) {
        log.info("Delete billing cycle request received for ID: {}", id);
        billingCycleService.deleteBillingCycle(id);
        log.info("Billing cycle deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "BILLING_CYCLE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
