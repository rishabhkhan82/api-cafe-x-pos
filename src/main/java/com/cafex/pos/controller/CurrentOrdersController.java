package com.cafex.pos.controller;

import com.cafex.pos.dto.OrderResponse;
import com.cafex.pos.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/current-orders")
@RequiredArgsConstructor
@Slf4j
public class CurrentOrdersController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getCurrentOrders(@RequestParam(name = "restaurant_id", required = false) Long restaurantId) {
        log.info("Get current orders request received - restaurant_id: {}", restaurantId);
        List<OrderResponse> response = orderService.getCurrentOrders(restaurantId);
        log.info("Retrieved {} current orders for restaurant_id: {}", response.size(), restaurantId);
        return ResponseEntity.ok(response);
    }
}