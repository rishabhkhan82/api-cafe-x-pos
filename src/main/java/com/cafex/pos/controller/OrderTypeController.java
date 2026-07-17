package com.cafex.pos.controller;

import com.cafex.pos.dto.OrderTypePageResponse;
import com.cafex.pos.dto.OrderTypeRequest;
import com.cafex.pos.dto.OrderTypeResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.OrderTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OrderTypeController {

    private final OrderTypeService orderTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createOrderType(@Valid @RequestBody OrderTypeRequest request) {
        log.info("Create order type request received for key: {}", request.getKey());
        OrderTypeResponse response = orderTypeService.createOrderType(request);
        log.info("Order type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateOrderType(@PathVariable Long id, @Valid @RequestBody OrderTypeRequest request) {
        log.info("Update order type request received for ID: {}", id);
        OrderTypeResponse response = orderTypeService.updateOrderType(id, request);
        log.info("Order type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<OrderTypePageResponse> getOrderTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get order types request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        OrderTypePageResponse response = orderTypeService.getOrderTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} order types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderTypeResponse> getOrderTypeById(@PathVariable Long id) {
        log.info("Get order type by ID request received for ID: {}", id);
        OrderTypeResponse response = orderTypeService.getOrderTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order type not found"));
        log.info("Order type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteOrderType(@PathVariable Long id) {
        log.info("Delete order type request received for ID: {}", id);
        orderTypeService.deleteOrderType(id);
        log.info("Order type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "ORDER_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
