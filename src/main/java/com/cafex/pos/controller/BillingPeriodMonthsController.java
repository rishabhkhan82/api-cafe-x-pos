package com.cafex.pos.controller;

import com.cafex.pos.dto.BillingPeriodMonthsPageResponse;
import com.cafex.pos.dto.BillingPeriodMonthsRequest;
import com.cafex.pos.dto.BillingPeriodMonthsResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.BillingPeriodMonthsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing-period-months")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class BillingPeriodMonthsController {

    private final BillingPeriodMonthsService billingPeriodMonthsService;

    @PostMapping
    public ResponseEntity<OperationResponse> createBillingPeriodMonths(@Valid @RequestBody BillingPeriodMonthsRequest request) {
        log.info("Create billing period months request received for key: {}", request.getKey());
        BillingPeriodMonthsResponse response = billingPeriodMonthsService.createBillingPeriodMonths(request);
        log.info("Billing period months created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "BILLING_PERIOD_MONTHS_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateBillingPeriodMonths(@PathVariable Long id, @Valid @RequestBody BillingPeriodMonthsRequest request) {
        log.info("Update billing period months request received for ID: {}", id);
        BillingPeriodMonthsResponse response = billingPeriodMonthsService.updateBillingPeriodMonths(id, request);
        log.info("Billing period months updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "BILLING_PERIOD_MONTHS_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<BillingPeriodMonthsPageResponse> getBillingPeriodMonths(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get billing period months request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        BillingPeriodMonthsPageResponse response = billingPeriodMonthsService.getBillingPeriodMonthsWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} billing period months", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingPeriodMonthsResponse> getBillingPeriodMonthsById(@PathVariable Long id) {
        log.info("Get billing period months by ID request received for ID: {}", id);
        BillingPeriodMonthsResponse response = billingPeriodMonthsService.getBillingPeriodMonthsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing period months not found"));
        log.info("Billing period months retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteBillingPeriodMonths(@PathVariable Long id) {
        log.info("Delete billing period months request received for ID: {}", id);
        billingPeriodMonthsService.deleteBillingPeriodMonths(id);
        log.info("Billing period months deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "BILLING_PERIOD_MONTHS_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
