package com.cafex.pos.controller;

import com.cafex.pos.dto.InventoryStockLogRequest;
import com.cafex.pos.dto.InventoryStockLogResponse;
import com.cafex.pos.dto.InventoryStockLogSummaryResponse;
import com.cafex.pos.service.InventoryStockLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inventory-stock-logs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InventoryStockLogController {

    private final InventoryStockLogService inventoryStockLogService;

    @PostMapping
    public ResponseEntity<InventoryStockLogResponse> createStockLog(@Valid @RequestBody InventoryStockLogRequest request) {
        log.info("Create stock log request received for inventory item: {}", request.getInventoryItemId());
        InventoryStockLogResponse response = inventoryStockLogService.createStockLog(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStockLogs(
            @RequestParam("restaurant_id") Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long batch_id,
            @RequestParam(required = false) String search) {
        log.info("Get stock logs request for restaurant: {}", restaurantId);
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Map<String, Object> response = inventoryStockLogService.getStockLogsByRestaurant(restaurantId, pageable, type, batch_id, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryStockLogResponse> getStockLogById(@PathVariable Long id) {
        log.info("Get stock log by ID: {}", id);
        Optional<InventoryStockLogResponse> response = inventoryStockLogService.getStockLogById(id);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/summary")
    public ResponseEntity<InventoryStockLogSummaryResponse> getSummary(
            @RequestParam("restaurant_id") Long restaurantId) {
        log.info("Get stock log summary for restaurant: {}", restaurantId);
        InventoryStockLogSummaryResponse response = inventoryStockLogService.getSummaryByRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }
}
