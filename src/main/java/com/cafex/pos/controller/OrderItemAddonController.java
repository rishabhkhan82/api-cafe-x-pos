package com.cafex.pos.controller;

import com.cafex.pos.dto.OrderItemAddonRequest;
import com.cafex.pos.dto.OrderItemAddonResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.OrderItemAddonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OrderItemAddonController {

    private final OrderItemAddonService orderItemAddonService;

    @GetMapping("/order-items/{orderItemId}/addons")
    public ResponseEntity<List<OrderItemAddonResponse>> getAddonsByOrderItem(@PathVariable Long orderItemId) {
        log.info("Get add-ons for order item request received for orderItemId: {}", orderItemId);
        List<OrderItemAddonResponse> response = orderItemAddonService.getAddonsByOrderItemId(orderItemId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/order-items/{orderItemId}/addons")
    public ResponseEntity<OperationResponse> addAddon(@PathVariable Long orderItemId, @Valid @RequestBody OrderItemAddonRequest request) {
        log.info("Add add-on request received for order item {}", orderItemId);
        OrderItemAddonResponse response = orderItemAddonService.addAddonToOrderItem(orderItemId, request);
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_ITEM_ADDON_ADDED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/order-items/{orderItemId}/addons/{addonId}")
    public ResponseEntity<OperationResponse> updateAddon(@PathVariable Long orderItemId, @PathVariable Long addonId, @Valid @RequestBody OrderItemAddonRequest request) {
        log.info("Update add-on request received for addonId: {} on order item {}", addonId, orderItemId);
        OrderItemAddonResponse response = orderItemAddonService.updateOrderItemAddon(orderItemId, addonId, request);
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_ITEM_ADDON_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @DeleteMapping("/order-items/{orderItemId}/addons/{addonId}")
    public ResponseEntity<OperationResponse> deleteAddon(@PathVariable Long orderItemId, @PathVariable Long addonId) {
        log.info("Delete add-on request received for addonId: {} from order item {}", addonId, orderItemId);
        orderItemAddonService.deleteOrderItemAddon(orderItemId, addonId);
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_ITEM_ADDON_DELETED", addonId, null);
        return ResponseEntity.ok(operationResponse);
    }

    @DeleteMapping("/order-items/{orderItemId}/addons")
    public ResponseEntity<OperationResponse> deleteAllByOrderItem(@PathVariable Long orderItemId) {
        log.info("Delete all add-ons for order item request received for orderItemId: {}", orderItemId);
        orderItemAddonService.deleteByOrderItemId(orderItemId);
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_ITEM_ADDONS_DELETED", orderItemId, null);
        return ResponseEntity.ok(operationResponse);
    }
}
