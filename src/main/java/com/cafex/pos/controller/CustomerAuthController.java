package com.cafex.pos.controller;

import com.cafex.pos.dto.CustomerCreateRequest;
import com.cafex.pos.dto.CustomerLoginResponse;
import com.cafex.pos.dto.CustomerValidateResponse;
import com.cafex.pos.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth/customer")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CustomerAuthController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerLoginResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        log.info("Customer create request received for restaurantId: {}", request.getRestaurantId());

        CustomerLoginResponse response = customerService.createCustomer(request);
        log.info("Customer created successfully for restaurantId: {}", request.getRestaurantId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate/{restaurantId}/{customerId}")
    public ResponseEntity<?> validateCustomer(@PathVariable Long restaurantId, @PathVariable String customerId) {
        log.info("Customer validate request received for customerId: {}, restaurantId: {}", customerId, restaurantId);

        Optional<CustomerValidateResponse> responseOpt = customerService.validateCustomer(customerId, restaurantId);
        if (responseOpt.isPresent()) {
            log.info("Customer validated successfully for customerId: {}, restaurantId: {}", customerId, restaurantId);
            return ResponseEntity.ok(responseOpt.get());
        } else {
            log.warn("Customer not found or doesn't belong to restaurant - customerId: {}, restaurantId: {}", customerId, restaurantId);
            return ResponseEntity.notFound().build();
        }
    }
}