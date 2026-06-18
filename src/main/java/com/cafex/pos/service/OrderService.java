package com.cafex.pos.service;

import com.cafex.pos.dto.OrderRequest;
import com.cafex.pos.dto.OrderResponse;
import com.cafex.pos.dto.OrderItemRequest;
import com.cafex.pos.dto.OrderItemResponse;
import com.cafex.pos.entity.Order;
import com.cafex.pos.entity.OrderItem;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.entity.MenuItem;
import com.cafex.pos.repository.OrderRepository;
import com.cafex.pos.repository.OrderItemRepository;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.RestaurantRepository;
import com.cafex.pos.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getCurrentOrders(Long restaurantId) {
        log.info("Fetching current orders (not completed) - restaurant_id: {}", restaurantId);
        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.notEqual(root.get("status"), Order.OrderStatus.COMPLETED));
            if (restaurantId != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId));
            }
            return predicate;
        };
        List<Order> orders = orderRepository.findAll(spec);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderPageResponse getOrdersWithFilters(String orderId, String status, String customerName, String date, Long customerId, int page, int size) {
        log.info("Fetching orders with filters - orderId: {}, status: {}, customerName: {}, date: {}, page: {}, size: {}",
                orderId, status, customerName, date, page, size);

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

            // Date filter
            if (date != null && !date.trim().isEmpty()) {
                try {
                    LocalDate filterDate = LocalDate.parse(date);
                    LocalDateTime startOfDay = filterDate.atStartOfDay();
                    LocalDateTime endOfDay = filterDate.atTime(23, 59, 59, 999999999);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createdAt"), startOfDay, endOfDay));
                } catch (Exception e) {
                    log.warn("Invalid date filter: {}", date);
                }
            }

            // Customer ID filter
            if (customerId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("customer").get("id"), customerId));
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

        // Check if orderId already exists (only when provided)
        if (orderRequest.getOrderId() != null && !orderRequest.getOrderId().isBlank()
                && orderRepository.existsByOrderId(orderRequest.getOrderId())) {
            throw new RuntimeException("Order ID already exists: " + orderRequest.getOrderId());
        }

        // Load related entities
        Customer customer = null;
        if (orderRequest.getCustomerId() != null) {
            Optional<Customer> customerOpt = customerRepository.findById(orderRequest.getCustomerId());
            customer = customerOpt.orElse(null);
        }

        Restaurant restaurant = null;
        if (orderRequest.getRestaurantId() != null) {
            Optional<Restaurant> restaurantOpt = restaurantRepository.findById(orderRequest.getRestaurantId());
            restaurant = restaurantOpt.orElse(null);
        }

        Order order = new Order();
        order.setOrderId(orderRequest.getOrderId());
        order.setCustomer(customer);
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
        order.setDiscountAmount(orderRequest.getDiscountAmount());
        order.setLoyaltyDiscountAmount(orderRequest.getLoyaltyDiscountAmount());
        order.setRestaurant(restaurant);
        order.setInvoiceId(orderRequest.getInvoiceId());
        order.setCreatedAt(orderRequest.getCreatedAt() != null ? orderRequest.getCreatedAt() : LocalDateTime.now());
        order.setUpdatedAt(orderRequest.getUpdatedAt() != null ? orderRequest.getUpdatedAt() : LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Auto-generate orderId if not provided: ORD-{id zero-padded to 4 digits}
        if (savedOrder.getOrderId() == null || savedOrder.getOrderId().isBlank()) {
            String generatedOrderId = String.format("ORD-%04d", savedOrder.getId());
            savedOrder.setOrderId(generatedOrderId);
            orderRepository.save(savedOrder);
        }

        // Save order items
        if (orderRequest.getOrderItems() != null && !orderRequest.getOrderItems().isEmpty()) {
            for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);

                // Load menu item entity
                if (itemRequest.getMenuItemId() != null) {
                    Optional<MenuItem> menuItemOpt = menuItemRepository.findById(itemRequest.getMenuItemId());
                    menuItemOpt.ifPresent(orderItem::setMenuItem);
                }

                orderItem.setMenuItemName(itemRequest.getMenuItemName());
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setUnitPrice(itemRequest.getUnitPrice());
                orderItem.setTotalPrice(itemRequest.getTotalPrice());
                orderItem.setCategory(itemRequest.getCategory());
                orderItem.setSpecialInstructions(itemRequest.getSpecialInstructions());
                orderItem.setStatus(itemRequest.getStatus());
                orderItemRepository.save(orderItem);
            }
        }

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
        existingOrder.setDiscountAmount(orderRequest.getDiscountAmount());
        existingOrder.setLoyaltyDiscountAmount(orderRequest.getLoyaltyDiscountAmount());
        existingOrder.setUpdatedAt(LocalDateTime.now());
        existingOrder.setInvoiceId(orderRequest.getInvoiceId());

        // Update order items in-place by matching ID from payload
        if (orderRequest.getOrderItems() != null && !orderRequest.getOrderItems().isEmpty()) {
            for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
                if (itemRequest.getId() != null) {
                    OrderItem existingItem = orderItemRepository.findById(itemRequest.getId()).orElse(null);
                    if (existingItem != null && existingItem.getOrder().getId().equals(id)) {
                        existingItem.setStatus(itemRequest.getStatus());
                        existingItem.setQuantity(itemRequest.getQuantity());
                        existingItem.setUnitPrice(itemRequest.getUnitPrice());
                        existingItem.setTotalPrice(itemRequest.getTotalPrice());
                        existingItem.setMenuItemName(itemRequest.getMenuItemName());
                        existingItem.setCategory(itemRequest.getCategory());
                        existingItem.setSpecialInstructions(itemRequest.getSpecialInstructions());
                        if (itemRequest.getMenuItemId() != null) {
                            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId()).orElse(null);
                            existingItem.setMenuItem(menuItem);
                        }
                        orderItemRepository.save(existingItem);
                    }
                }
            }
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("Order updated successfully with ID: {}", updatedOrder.getId());

        return convertToResponse(updatedOrder);
    }

    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }

        // Delete order items first
        orderItemRepository.deleteByOrderId(id);

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
        response.setDiscountAmount(order.getDiscountAmount());
        response.setLoyaltyDiscountAmount(order.getLoyaltyDiscountAmount());
        response.setRestaurantId(order.getRestaurant() != null ? order.getRestaurant().getId() : null);
        response.setInvoiceId(order.getInvoiceId());

        // Convert order items
        if (order.getItems() != null) {
            List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::convertOrderItemToResponse)
                .collect(Collectors.toList());
            response.setOrderItems(itemResponses);
        }

        return response;
    }

    private OrderItemResponse convertOrderItemToResponse(OrderItem orderItem) {
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

    public OrderPageResponse getOrdersForReports(String startDate, String endDate, String status, Long restaurantId) {
        log.info("Fetching orders for reports - startDate: {}, endDate: {}, status: {}, restaurantId: {}", startDate, endDate, status, restaurantId);

        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Date range filter
            if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
                try {
                    LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
                    LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59, 999999999);
                    predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.between(root.get("updatedAt"), startDateTime, endDateTime));
                } catch (Exception e) {
                    log.warn("Invalid date range filter - startDate: {}, endDate: {}", startDate, endDate);
                }
            }

            // Status filter (default to COMPLETED for reports)
            if (status != null && !status.trim().isEmpty()) {
                try {
                    Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), orderStatus));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter for reports: {}", status);
                }
            }

            // Restaurant filter
            if (restaurantId != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId));
            }

            return predicate;
        };

        List<Order> orders = orderRepository.findAll(spec);

        List<OrderResponse> content = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new OrderPageResponse(
            content,
            0, // currentPage (not used for reports)
            1, // pageCount (not used for reports)
            content.size() // totalRowCount
        );
    }

    public List<OrderResponse> getActiveOrdersForAuthenticatedCustomer() {
        log.info("Fetching active orders for authenticated customer");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerIdentifier = auth.getName();

        Customer customer = customerRepository.findByCustomerId(customerIdentifier)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerIdentifier));

        List<Order> allOrders = orderRepository.findByCustomerId(customer.getId());
        List<Order> activeOrders = allOrders.stream()
                .filter(order -> order.getStatus() != Order.OrderStatus.COMPLETED
                        && order.getStatus() != Order.OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        return activeOrders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}