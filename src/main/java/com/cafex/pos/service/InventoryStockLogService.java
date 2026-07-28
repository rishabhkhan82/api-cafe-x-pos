package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryStockLogRequest;
import com.cafex.pos.dto.InventoryStockLogResponse;
import com.cafex.pos.dto.InventoryStockLogSummaryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InventoryStockLogService {
    InventoryStockLogResponse createStockLog(InventoryStockLogRequest request);
    Map<String, Object> getStockLogsByRestaurant(Long restaurantId, Pageable pageable, String type, Long batchId, String search);
    InventoryStockLogSummaryResponse getSummaryByRestaurant(Long restaurantId);
    Optional<InventoryStockLogResponse> getStockLogById(Long id);
}
