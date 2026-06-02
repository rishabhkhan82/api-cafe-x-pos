package com.cafex.pos.controller;

import com.cafex.pos.dto.CustomerRequest;
import com.cafex.pos.dto.CustomerResponse;
import com.cafex.pos.dto.CustomerPageResponse;
import com.cafex.pos.dto.CustomerWithTokenResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        log.info("Save customer request received for name: {}", customerRequest.getName());
        try {
            CustomerResponse response = customerService.saveCustomer(customerRequest);
            log.info("Customer saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "CUSTOMER_CREATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save customer: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "CUSTOMER_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest customerRequest) {
        log.info("Update customer request received for ID: {}", id);
        try {
            CustomerResponse response = customerService.updateCustomer(id, customerRequest);
            log.info("Customer updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "CUSTOMER_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update customer: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "CUSTOMER_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<CustomerPageResponse> getCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get customers request received with filters - name: {}, restaurantId: {}, email: {}, page: {}, size: {}",
                name, restaurantId, email, page, size);
        try {
            CustomerPageResponse response = customerService.getCustomers(name, restaurantId, email, page, size);
            log.info("Retrieved {} customers (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get customers: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerWithTokenResponse> getCustomerById(@PathVariable Long id) {
        log.info("Get customer by ID request received for ID: {}", id);
        try {
            CustomerWithTokenResponse response = customerService.getCustomerWithTokenById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            log.info("Customer retrieved successfully with ID: {}", response.getCustomer().getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/by-customer-id/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerByCustomerId(@PathVariable String customerId) {
        log.info("Get customer by customerId request received for customerId: {}", customerId);
        try {
            CustomerResponse response = customerService.getCustomerByCustomerId(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            log.info("Customer retrieved successfully with customerId: {}", response.getCustomerId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteCustomer(@PathVariable Long id) {
        log.info("Delete customer request received for ID: {}", id);
        try {
            customerService.deleteCustomer(id);
            log.info("Customer deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "CUSTOMER_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete customer: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "CUSTOMER_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}