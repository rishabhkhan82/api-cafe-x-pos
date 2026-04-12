package com.cafex.pos.service;

import com.cafex.pos.dto.OrderItemRequest;
import com.cafex.pos.dto.OrderItemResponse;
import com.cafex.pos.entity.Order;
import com.cafex.pos.entity.OrderItem;
import com.cafex.pos.repository.OrderItemRepository;
import com.cafex.pos.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public List<OrderItemResponse> getAllOrderItems() {
        log.info("Fetching all order items");
        List<OrderItem> orderItems = orderItemRepository.findAll();
        return orderItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderItemResponse> getOrderItemsWithFilters(Long orderId, String status) {
        log.info("Fetching order items with filters - orderId: {}, status: {}", orderId, status);

        List<OrderItem> orderItems;

        if (orderId != null) {
            orderItems = orderItemRepository.findByOrderId(orderId);
        } else {
            orderItems = orderItemRepository.findAll();
        }

        // Filter by status if provided
        if (status != null && !status.trim().isEmpty()) {
            orderItems = orderItems.stream()
                    .filter(item -> status.equals(item.getStatus()))
                    .collect(Collectors.toList());
        }

        return orderItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<OrderItemResponse> getOrderItemById(Long id) {
        log.info("Fetching order item by ID: {}", id);
        return orderItemRepository.findById(id)
                .map(this::convertToResponse);
    }

    public OrderItemResponse saveOrderItem(OrderItemRequest orderItemRequest) {
        log.info("Saving new order item: {}", orderItemRequest.getMenuItemName());

        // Validate order exists
        Order order = orderRepository.findById(orderItemRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderItemRequest.getOrderId()));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItemName(orderItemRequest.getMenuItemName());
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setUnitPrice(orderItemRequest.getUnitPrice());
        orderItem.setTotalPrice(orderItemRequest.getTotalPrice());
        orderItem.setCategory(orderItemRequest.getCategory());
        orderItem.setSpecialInstructions(orderItemRequest.getSpecialInstructions());
        orderItem.setStatus(orderItemRequest.getStatus());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        log.info("Order item saved successfully with ID: {}", savedOrderItem.getId());

        return convertToResponse(savedOrderItem);
    }

    public OrderItemResponse updateOrderItem(Long id, OrderItemRequest orderItemRequest) {
        log.info("Updating order item with ID: {}", id);

        OrderItem existingOrderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + id));

        // Validate order exists if orderId is provided
        if (orderItemRequest.getOrderId() != null) {
            Order order = orderRepository.findById(orderItemRequest.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderItemRequest.getOrderId()));
            existingOrderItem.setOrder(order);
        }

        // Update fields
        existingOrderItem.setMenuItemName(orderItemRequest.getMenuItemName());
        existingOrderItem.setQuantity(orderItemRequest.getQuantity());
        existingOrderItem.setUnitPrice(orderItemRequest.getUnitPrice());
        existingOrderItem.setTotalPrice(orderItemRequest.getTotalPrice());
        existingOrderItem.setCategory(orderItemRequest.getCategory());
        existingOrderItem.setSpecialInstructions(orderItemRequest.getSpecialInstructions());
        existingOrderItem.setStatus(orderItemRequest.getStatus());

        OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
        log.info("Order item updated successfully with ID: {}", updatedOrderItem.getId());

        return convertToResponse(updatedOrderItem);
    }

    public void deleteOrderItem(Long id) {
        log.info("Deleting order item with ID: {}", id);

        if (!orderItemRepository.existsById(id)) {
            throw new RuntimeException("Order item not found with ID: " + id);
        }

        orderItemRepository.deleteById(id);
        log.info("Order item deleted successfully with ID: {}", id);
    }

    private OrderItemResponse convertToResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setOrderId(orderItem.getOrder().getId());
        response.setMenuItemId(orderItem.getMenuItem() != null ? orderItem.getMenuItem().getId() : null);
        response.setMenuItemName(orderItem.getMenuItemName());
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setTotalPrice(orderItem.getTotalPrice());
        response.setCategory(orderItem.getCategory());
        response.setSpecialInstructions(orderItem.getSpecialInstructions());
        response.setStatus(orderItem.getStatus());
        return response;
    }
}