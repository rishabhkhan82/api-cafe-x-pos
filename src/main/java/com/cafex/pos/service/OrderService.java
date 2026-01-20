package com.cafex.pos.service;

import com.cafex.pos.dto.OrderRequest;
import com.cafex.pos.dto.OrderResponse;
import com.cafex.pos.entity.Order;
import com.cafex.pos.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.cafex.pos.dto.OrderPageResponse;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderPageResponse getOrdersWithFilters(String orderId, String status, String customerName, int page, int size) {
        log.info("Fetching orders with filters - orderId: {}, status: {}, customerName: {}, page: {}, size: {}",
                orderId, status, customerName, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Order ID filter
            if (orderId != null && !orderId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("orderId")), "%" + orderId.toLowerCase() + "%"));
            }

            // Status filter
            if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
                try {
                    Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), orderStatus));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter: {}", status);
                }
            }

            // Customer name filter
            if (customerName != null && !customerName.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("customerName")), "%" + customerName.toLowerCase() + "%"));
            }

            return predicate;
        };

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        List<OrderResponse> content = orderPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new OrderPageResponse(
            content,
            orderPage.getNumber() + 1, // currentPage (1-based)
            orderPage.getTotalPages(),
            orderPage.getTotalElements()
        );
    }

    public Optional<OrderResponse> getOrderById(Long id) {
        log.info("Fetching order by ID: {}", id);
        return orderRepository.findById(id)
                .map(this::convertToResponse);
    }

    public OrderResponse saveOrder(OrderRequest orderRequest) {
        log.info("Saving new order: {}", orderRequest.getOrderId());

        // Check if orderId already exists
        if (orderRepository.existsByOrderId(orderRequest.getOrderId())) {
            throw new RuntimeException("Order ID already exists: " + orderRequest.getOrderId());
        }

        Order order = new Order();
        order.setOrderId(orderRequest.getOrderId());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setTableNumber(orderRequest.getTableNumber());
        order.setStatus(orderRequest.getStatus());
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setSpecialInstructions(orderRequest.getSpecialInstructions());
        order.setPaymentStatus(orderRequest.getPaymentStatus());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setOrderType(orderRequest.getOrderType());
        order.setEstimatedReadyTime(orderRequest.getEstimatedReadyTime());
        order.setDeliveredAt(orderRequest.getDeliveredAt());
        order.setPriority(orderRequest.getPriority());
        order.setTaxAmount(orderRequest.getTaxAmount());
        order.setCreatedAt(orderRequest.getCreatedAt() != null ? orderRequest.getCreatedAt() : LocalDateTime.now());
        order.setUpdatedAt(orderRequest.getUpdatedAt() != null ? orderRequest.getUpdatedAt() : LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved successfully with ID: {}", savedOrder.getId());

        return convertToResponse(savedOrder);
    }

    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        log.info("Updating order with ID: {}", id);

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        // Check orderId uniqueness if changed
        if (!existingOrder.getOrderId().equals(orderRequest.getOrderId()) &&
            orderRepository.existsByOrderId(orderRequest.getOrderId())) {
            throw new RuntimeException("Order ID already exists: " + orderRequest.getOrderId());
        }

        // Update fields
        existingOrder.setOrderId(orderRequest.getOrderId());
        existingOrder.setCustomerName(orderRequest.getCustomerName());
        existingOrder.setTableNumber(orderRequest.getTableNumber());
        existingOrder.setStatus(orderRequest.getStatus());
        existingOrder.setTotalAmount(orderRequest.getTotalAmount());
        existingOrder.setSpecialInstructions(orderRequest.getSpecialInstructions());
        existingOrder.setPaymentStatus(orderRequest.getPaymentStatus());
        existingOrder.setPaymentMethod(orderRequest.getPaymentMethod());
        existingOrder.setOrderType(orderRequest.getOrderType());
        existingOrder.setEstimatedReadyTime(orderRequest.getEstimatedReadyTime());
        existingOrder.setDeliveredAt(orderRequest.getDeliveredAt());
        existingOrder.setPriority(orderRequest.getPriority());
        existingOrder.setTaxAmount(orderRequest.getTaxAmount());
        existingOrder.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("Order updated successfully with ID: {}", updatedOrder.getId());

        return convertToResponse(updatedOrder);
    }

    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }

    public boolean existsByOrderId(String orderId) {
        return orderRepository.existsByOrderId(orderId);
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderId(order.getOrderId());
        response.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        response.setCustomerName(order.getCustomerName());
        response.setTableNumber(order.getTableNumber());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setSpecialInstructions(order.getSpecialInstructions());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setOrderType(order.getOrderType());
        response.setEstimatedReadyTime(order.getEstimatedReadyTime());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setPriority(order.getPriority());
        response.setTaxAmount(order.getTaxAmount());
        response.setRestaurantId(order.getRestaurant() != null ? order.getRestaurant().getId() : null);
        return response;
    }
}