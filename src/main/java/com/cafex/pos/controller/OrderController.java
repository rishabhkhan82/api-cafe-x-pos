package com.cafex.pos.controller;

import com.cafex.pos.dto.OrderRequest;
import com.cafex.pos.dto.OrderResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.OrderPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
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
        OrderResponse response = orderService.saveOrder(orderRequest);
        log.info("Order saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest) {
        log.info("Update order request received for ID: {}", id);
        OrderResponse response = orderService.updateOrder(id, orderRequest);
        log.info("Order updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<OrderPageResponse> getOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get orders request received with filters - orderId: {}, status: {}, customerName: {}, date: {}, customerId: {}, page: {}, size: {}",
                orderId, status, customerName, date, customerId, page, size);
        OrderPageResponse response = orderService.getOrdersWithFilters(orderId, status, customerName, date, customerId, page, size);
        log.info("Retrieved {} orders (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrdersForCustomer() {
        log.info("Get active orders for authenticated customer");
        List<OrderResponse> response = orderService.getActiveOrdersForAuthenticatedCustomer();
        log.info("Retrieved {} active orders for customer", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    public ResponseEntity<OrderPageResponse> getOrdersForReports(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "COMPLETED") String status,
            @RequestParam(name = "restaurant_id", required = false) Long restaurantId) {
        log.info("Get orders for reports request received - startDate: {}, endDate: {}, status: {}, restaurantId: {}", startDate, endDate, status, restaurantId);
        OrderPageResponse response = orderService.getOrdersForReports(startDate, endDate, status, restaurantId);
        log.info("Retrieved {} orders for reports", response.getData().size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("Get order by ID request received for ID: {}", id);
        OrderResponse response = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        log.info("Order retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteOrder(@PathVariable Long id) {
        log.info("Delete order request received for ID: {}", id);
        orderService.deleteOrder(id);
        log.info("Order deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }


}