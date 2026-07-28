package com.cafex.pos.controller;

import com.cafex.pos.dto.OrderItemRequest;
import com.cafex.pos.dto.OrderItemResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveOrderItem(@Valid @RequestBody OrderItemRequest orderItemRequest) {
        log.info("Save order item request received for menu item: {}", orderItemRequest.getMenuItemName());
        OrderItemResponse response = orderItemService.saveOrderItem(orderItemRequest);
        log.info("Order item saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_ITEM_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItemRequest orderItemRequest) {
        log.info("Update order item request received for ID: {}", id);
        OrderItemResponse response = orderItemService.updateOrderItem(id, orderItemRequest);
        log.info("Order item updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_ITEM_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String status) {
        log.info("Get order items request received with filters - orderId: {}, status: {}", orderId, status);
        List<OrderItemResponse> response = orderItemService.getOrderItemsWithFilters(orderId, status);
        log.info("Retrieved {} order items", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
        log.info("Get order item by ID request received for ID: {}", id);
        OrderItemResponse response = orderItemService.getOrderItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        log.info("Order item retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteOrderItem(@PathVariable Long id) {
        log.info("Delete order item request received for ID: {}", id);
        orderItemService.deleteOrderItem(id);
        log.info("Order item deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_ITEM_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}