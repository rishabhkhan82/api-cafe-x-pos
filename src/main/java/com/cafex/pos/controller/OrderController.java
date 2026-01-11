package com.cafex.pos.controller;

import com.cafex.pos.dto.OrderRequest;
import com.cafex.pos.dto.OrderResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.OrderPageResponse;
import com.cafex.pos.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveOrder(@Valid @RequestBody OrderRequest orderRequest) {
        log.info("Save order request received for orderId: {}", orderRequest.getOrderId());
        try {
            OrderResponse response = orderService.saveOrder(orderRequest);
            log.info("Order saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "ORDER_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save order: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "ORDER_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest) {
        log.info("Update order request received for ID: {}", id);
        try {
            OrderResponse response = orderService.updateOrder(id, orderRequest);
            log.info("Order updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "ORDER_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update order: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "ORDER_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<OrderPageResponse> getOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get orders request received with filters - orderId: {}, status: {}, customerName: {}, page: {}, size: {}",
                orderId, status, customerName, page, size);
        try {
            OrderPageResponse response = orderService.getOrdersWithFilters(orderId, status, customerName, page, size);
            log.info("Retrieved {} orders (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get orders: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("Get order by ID request received for ID: {}", id);
        try {
            OrderResponse response = orderService.getOrderById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            log.info("Order retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteOrder(@PathVariable Long id) {
        log.info("Delete order request received for ID: {}", id);
        try {
            orderService.deleteOrder(id);
            log.info("Order deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "ORDER_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete order: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "ORDER_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}