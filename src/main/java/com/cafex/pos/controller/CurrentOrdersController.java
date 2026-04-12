package com.cafex.pos.controller;

import com.cafex.pos.dto.OrderResponse;
import com.cafex.pos.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/current-orders")
@RequiredArgsConstructor
@Slf4j
public class CurrentOrdersController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getCurrentOrders() {
        log.info("Get current orders request received");
        try {
            List<OrderResponse> response = orderService.getCurrentOrders();
            log.info("Retrieved {} current orders", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get current orders: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}