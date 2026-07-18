package com.cafex.pos.service;

import com.cafex.pos.dto.OrderTypePageResponse;
import com.cafex.pos.dto.OrderTypeRequest;
import com.cafex.pos.dto.OrderTypeResponse;
import com.cafex.pos.entity.OrderTypeMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.OrderTypeMasterRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderTypeServiceImpl implements OrderTypeService {

    private final OrderTypeMasterRepository orderTypeMasterRepository;

    @Override
    public OrderTypePageResponse getOrderTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching order types with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        OrderTypePageResponse allResponse = new OrderTypePageResponse();

        Specification<OrderTypeMaster> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
            }

            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        if (page == 0 && size == 0) {
            List<OrderTypeMaster> filteredOrders = orderTypeMasterRepository.findAll(spec);
            List<OrderTypeResponse> content = filteredOrders.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<OrderTypeMaster> orderPage = orderTypeMasterRepository.findAll(spec, pageable);

        List<OrderTypeResponse> content = orderPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new OrderTypePageResponse(
                content,
                orderPage.getNumber() + 1,
                orderPage.getTotalPages(),
                orderPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderTypeResponse> getAllOrderTypes() {
        log.info("Fetching all order types");
        List<OrderTypeMaster> orders = orderTypeMasterRepository.findAll();
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderTypeResponse> getOrderTypeById(Long id) {
        log.info("Fetching order type by ID: {}", id);
        return orderTypeMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public OrderTypeResponse createOrderType(OrderTypeRequest request) {
        log.info("Creating new order type: {}", request.getName());

        if (orderTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Order type key already exists: " + request.getKey());
        }

        OrderTypeMaster order = new OrderTypeMaster();
        order.setName(request.getName());
        order.setKey(request.getKey());
        order.setDescription(request.getDescription());
        order.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        order.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        order.setCreatedBy(request.getCreatedBy());
        order.setUpdatedBy(request.getUpdatedBy());

        OrderTypeMaster savedOrder = orderTypeMasterRepository.save(order);
        log.info("Order type created successfully with ID: {}", savedOrder.getId());

        return convertToResponse(savedOrder);
    }

    @Override
    public OrderTypeResponse updateOrderType(Long id, OrderTypeRequest request) {
        log.info("Updating order type with ID: {}", id);

        OrderTypeMaster existingOrder = orderTypeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order type not found with ID: " + id));

        if (!existingOrder.getKey().equals(request.getKey()) &&
                orderTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Order type key already exists: " + request.getKey());
        }

        existingOrder.setName(request.getName());
        existingOrder.setKey(request.getKey());
        existingOrder.setDescription(request.getDescription());
        existingOrder.setIsActive(request.getIsActive());
        existingOrder.setDisplayOrder(request.getDisplayOrder());
        existingOrder.setUpdatedBy(request.getUpdatedBy());
        existingOrder.setUpdatedAt(LocalDateTime.now());

        OrderTypeMaster updatedOrder = orderTypeMasterRepository.save(existingOrder);
        log.info("Order type updated successfully with ID: {}", updatedOrder.getId());

        return convertToResponse(updatedOrder);
    }

    @Override
    public void deleteOrderType(Long id) {
        log.info("Deleting order type with ID: {}", id);

        if (!orderTypeMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order type not found with ID: " + id);
        }

        orderTypeMasterRepository.deleteById(id);
        log.info("Order type deleted successfully with ID: {}", id);
    }

    private OrderTypeResponse convertToResponse(OrderTypeMaster order) {
        OrderTypeResponse response = new OrderTypeResponse();
        response.setId(order.getId());
        response.setName(order.getName());
        response.setKey(order.getKey());
        response.setDescription(order.getDescription());
        response.setIsActive(order.getIsActive());
        response.setDisplayOrder(order.getDisplayOrder());
        response.setCreatedBy(order.getCreatedBy());
        response.setUpdatedBy(order.getUpdatedBy());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}
