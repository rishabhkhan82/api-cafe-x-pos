package com.cafex.pos.service;

import com.cafex.pos.dto.OrderTypePageResponse;
import com.cafex.pos.dto.OrderTypeRequest;
import com.cafex.pos.dto.OrderTypeResponse;
import java.util.List;
import java.util.Optional;

public interface OrderTypeService {
    OrderTypePageResponse getOrderTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<OrderTypeResponse> getAllOrderTypes();
    Optional<OrderTypeResponse> getOrderTypeById(Long id);
    OrderTypeResponse createOrderType(OrderTypeRequest request);
    OrderTypeResponse updateOrderType(Long id, OrderTypeRequest request);
    void deleteOrderType(Long id);
}
