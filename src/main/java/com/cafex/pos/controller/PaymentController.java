package com.cafex.pos.controller;

import com.cafex.pos.dto.PaymentOrderRequest;
import com.cafex.pos.dto.PaymentOrderResponse;
import com.cafex.pos.service.PaymentService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@Valid @RequestBody PaymentOrderRequest request) throws RazorpayException {
        log.info("Create Razorpay order request for planId: {}, months: {}, amount: {}", request.getPlanId(), request.getMonths(), request.getCalculatedAmount());
        PaymentOrderResponse response = paymentService.createOrder(request);
        log.info("Razorpay order created with ID: {}", response.getOrderId());
        return ResponseEntity.ok(response);
    }
}