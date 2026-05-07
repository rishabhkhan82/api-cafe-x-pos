package com.cafex.pos.controller;

import com.cafex.pos.dto.CustomerCreateRequest;
import com.cafex.pos.dto.CustomerLoginResponse;
import com.cafex.pos.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        try {
            CustomerLoginResponse response = customerService.createCustomer(request);
            log.info("Customer created successfully for restaurantId: {}", request.getRestaurantId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Customer creation failed for restaurantId: {} - {}", request.getRestaurantId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}