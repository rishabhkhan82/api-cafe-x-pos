package com.cafex.pos.service;

import com.cafex.pos.dto.OrderItemAddonRequest;
import com.cafex.pos.dto.OrderItemAddonResponse;
import com.cafex.pos.entity.OrderItem;
import com.cafex.pos.entity.OrderItemAddon;
import com.cafex.pos.repository.OrderItemAddonRepository;
import com.cafex.pos.repository.OrderItemRepository;
import com.cafex.pos.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderItemAddonService {

    private final OrderItemAddonRepository orderItemAddonRepository;
    private final OrderItemRepository orderItemRepository;

    public List<OrderItemAddonResponse> getAddonsByOrderItemId(Long orderItemId) {
        log.info("Fetching addons for order item ID: {}", orderItemId);
        return orderItemAddonRepository.findByOrderItemId(orderItemId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderItemAddonResponse addAddonToOrderItem(Long orderItemId, OrderItemAddonRequest request) {
        log.info("Adding addon to order item ID: {}", orderItemId);

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with ID: " + orderItemId));

        OrderItemAddon addon = new OrderItemAddon();
        addon.setOrderItem(orderItem);
        addon.setAddonId(request.getAddonId());
        addon.setAddonName(request.getAddonName());
        addon.setAddonPrice(request.getAddonPrice());
        addon.setQuantity(request.getQuantity());
        addon.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : false);
        addon.setMinQuantity(request.getMinQuantity() != null ? request.getMinQuantity() : 0);
        addon.setMaxQuantity(request.getMaxQuantity() != null ? request.getMaxQuantity() : 10);
        addon.setCreatedAt(LocalDateTime.now());
        addon.setUpdatedAt(LocalDateTime.now());

        OrderItemAddon savedAddon = orderItemAddonRepository.save(addon);
        log.info("Addon added successfully with ID: {}", savedAddon.getId());

        return convertToResponse(savedAddon);
    }

    public OrderItemAddonResponse updateOrderItemAddon(Long orderItemId, Long addonId, OrderItemAddonRequest request) {
        log.info("Updating addon ID: {} for order item ID: {}", addonId, orderItemId);

        OrderItemAddon addon = orderItemAddonRepository.findById(addonId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item addon not found with ID: " + addonId));

        if (!addon.getOrderItem().getId().equals(orderItemId)) {
            throw new ResourceNotFoundException("Addon ID " + addonId + " does not belong to order item ID: " + orderItemId);
        }

        addon.setQuantity(request.getQuantity());
        addon.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : addon.getIsRequired());
        addon.setMinQuantity(request.getMinQuantity() != null ? request.getMinQuantity() : addon.getMinQuantity());
        addon.setMaxQuantity(request.getMaxQuantity() != null ? request.getMaxQuantity() : addon.getMaxQuantity());
        addon.setUpdatedAt(LocalDateTime.now());

        OrderItemAddon updatedAddon = orderItemAddonRepository.save(addon);
        log.info("Addon updated successfully with ID: {}", updatedAddon.getId());

        return convertToResponse(updatedAddon);
    }

    public void deleteOrderItemAddon(Long orderItemId, Long addonId) {
        log.info("Deleting addon ID: {} from order item ID: {}", addonId, orderItemId);

        OrderItemAddon addon = orderItemAddonRepository.findById(addonId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item addon not found with ID: " + addonId));

        if (!addon.getOrderItem().getId().equals(orderItemId)) {
            throw new ResourceNotFoundException("Addon ID " + addonId + " does not belong to order item ID: " + orderItemId);
        }

        orderItemAddonRepository.delete(addon);
        log.info("Addon deleted successfully with ID: {}", addonId);
    }

    public void deleteByOrderItemId(Long orderItemId) {
        log.info("Deleting all addons for order item ID: {}", orderItemId);
        orderItemAddonRepository.deleteByOrderItemId(orderItemId);
    }

    public void saveAllForOrderItem(OrderItem orderItem, List<OrderItemAddonRequest> addonRequests) {
        if (addonRequests == null || addonRequests.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (OrderItemAddonRequest request : addonRequests) {
            OrderItemAddon addon = new OrderItemAddon();
            addon.setOrderItem(orderItem);
            addon.setAddonId(request.getAddonId());
            addon.setAddonName(request.getAddonName());
            addon.setAddonPrice(request.getAddonPrice());
            addon.setQuantity(request.getQuantity());
            addon.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : false);
            addon.setMinQuantity(request.getMinQuantity() != null ? request.getMinQuantity() : 0);
            addon.setMaxQuantity(request.getMaxQuantity() != null ? request.getMaxQuantity() : 10);
            addon.setCreatedAt(now);
            addon.setUpdatedAt(now);
            orderItemAddonRepository.save(addon);
        }
    }

    private OrderItemAddonResponse convertToResponse(OrderItemAddon addon) {
        OrderItemAddonResponse response = new OrderItemAddonResponse();
        response.setId(addon.getId());
        response.setOrderItemId(addon.getOrderItem() != null ? addon.getOrderItem().getId() : null);
        response.setAddonId(addon.getAddonId());
        response.setAddonName(addon.getAddonName());
        response.setAddonPrice(addon.getAddonPrice());
        response.setQuantity(addon.getQuantity());
        response.setIsRequired(addon.getIsRequired());
        response.setMinQuantity(addon.getMinQuantity());
        response.setMaxQuantity(addon.getMaxQuantity());
        return response;
    }
}
