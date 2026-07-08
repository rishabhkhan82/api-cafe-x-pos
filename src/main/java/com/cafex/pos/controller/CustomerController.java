package com.cafex.pos.controller;

import com.cafex.pos.dto.CustomerRequest;
import com.cafex.pos.dto.CustomerResponse;
import com.cafex.pos.dto.CustomerPageResponse;
import com.cafex.pos.dto.CustomerWithTokenResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
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
        CustomerResponse response = customerService.saveCustomer(customerRequest);
        log.info("Customer saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "CUSTOMER_CREATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest customerRequest) {
        log.info("Update customer request received for ID: {}", id);
        CustomerResponse response = customerService.updateCustomer(id, customerRequest);
        log.info("Customer updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "CUSTOMER_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
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
        CustomerPageResponse response = customerService.getCustomers(name, restaurantId, email, page, size);
        log.info("Retrieved {} customers (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerWithTokenResponse> getCustomerById(@PathVariable Long id) {
        log.info("Get customer by ID request received for ID: {}", id);
        CustomerWithTokenResponse response = customerService.getCustomerWithTokenById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        log.info("Customer retrieved successfully with ID: {}", response.getCustomer().getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-customer-id/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerByCustomerId(@PathVariable String customerId) {
        log.info("Get customer by customerId request received for customerId: {}", customerId);
        CustomerResponse response = customerService.getCustomerByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        log.info("Customer retrieved successfully with customerId: {}", response.getCustomerId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteCustomer(@PathVariable Long id) {
        log.info("Delete customer request received for ID: {}", id);
        customerService.deleteCustomer(id);
        log.info("Customer deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "CUSTOMER_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}