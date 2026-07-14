package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryStockLogRequest;
import com.cafex.pos.dto.InventoryStockLogResponse;
import com.cafex.pos.dto.InventoryStockLogSummaryResponse;
import com.cafex.pos.entity.InventoryStockLog;
import com.cafex.pos.repository.InventoryStockLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryStockLogServiceImpl implements InventoryStockLogService {

    private final InventoryStockLogRepository inventoryStockLogRepository;
    private final EntityManager entityManager;

    @Override
    public InventoryStockLogResponse createStockLog(InventoryStockLogRequest request) {
        log.info("Creating stock log for inventory item: {}", request.getInventoryItemId());

        InventoryStockLog stockLog = new InventoryStockLog();
        stockLog.setInventoryItemId(request.getInventoryItemId());
        stockLog.setInventoryItemName(request.getInventoryItemName());
        stockLog.setRestaurantId(request.getRestaurantId());
        stockLog.setQuantityChange(request.getQuantityChange());
        stockLog.setBalanceAfter(request.getBalanceAfter());
        stockLog.setType(request.getType());
        stockLog.setReferenceId(request.getReferenceId());
        stockLog.setReferenceType(request.getReferenceType());
        stockLog.setBatchId(request.getBatchId());
        stockLog.setNote(request.getNote());
        stockLog.setCreatedBy(request.getCreatedBy());

        InventoryStockLog saved = inventoryStockLogRepository.save(stockLog);
        log.info("Stock log created with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    @Override
    public InventoryStockLogSummaryResponse getSummaryByRestaurant(Long restaurantId) {
        log.info("Fetching stock log summary for restaurant: {}", restaurantId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<InventoryStockLog> root = query.from(InventoryStockLog.class);

        query.select(cb.array(
            cb.count(root),
            cb.sum(cb.<Number>selectCase()
                .when(cb.equal(root.get("type"), "SALE"), 1)
                .otherwise(0)),
            cb.sum(cb.<Number>selectCase()
                .when(cb.equal(root.get("type"), "PRODUCTION"), 1)
                .otherwise(0)),
            cb.sum(cb.<Number>selectCase()
                .when(cb.equal(root.get("type"), "WASTAGE"), 1)
                .otherwise(0)),
            cb.sum(cb.<Number>selectCase()
                .when(cb.equal(root.get("type"), "ADJUSTMENT"), 1)
                .otherwise(0)),
            cb.sum(cb.<Number>selectCase()
                .when(cb.equal(root.get("type"), "PURCHASE"), 1)
                .otherwise(0))
        ));

        query.where(cb.equal(root.get("restaurantId"), restaurantId));

        List<Object[]> results = entityManager.createQuery(query).getResultList();
        Object[] row = results.isEmpty() ? new Object[0] : results.get(0);

        long totalLogs = row[0] != null ? ((Number) row[0]).longValue() : 0;
        long sales = row[1] != null ? ((Number) row[1]).longValue() : 0;
        long production = row[2] != null ? ((Number) row[2]).longValue() : 0;
        long waste = row[3] != null ? ((Number) row[3]).longValue() : 0;
        long adjustments = row[4] != null ? ((Number) row[4]).longValue() : 0;
        long purchases = row[5] != null ? ((Number) row[5]).longValue() : 0;

        return new InventoryStockLogSummaryResponse(totalLogs, sales, production, waste, adjustments, purchases);
    }

    @Override
    public Map<String, Object> getStockLogsByRestaurant(Long restaurantId, Pageable pageable, String type, Long batchId, String search) {
        log.info("Fetching stock logs for restaurant: {}, type: {}, batchId: {}", restaurantId, type, batchId);
        Page<InventoryStockLog> page = inventoryStockLogRepository.findAll((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.equal(root.get("restaurantId"), restaurantId)
            );
            if (type != null && !type.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), type));
            }
            if (batchId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("batchId"), batchId));
            }
            if (search != null && !search.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("inventoryItemName")), "%" + search.toLowerCase() + "%"));
            }
            return predicate;
        }, pageable);

        List<InventoryStockLogResponse> data = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("currentPage", page.getNumber() + 1);
        response.put("pageCount", page.getTotalPages());
        response.put("totalRowCount", page.getTotalElements());
        return response;
    }

    @Override
    public Optional<InventoryStockLogResponse> getStockLogById(Long id) {
        log.info("Fetching stock log by ID: {}", id);
        return inventoryStockLogRepository.findById(id)
                .map(this::convertToResponse);
    }

    private InventoryStockLogResponse convertToResponse(InventoryStockLog stockLog) {
        InventoryStockLogResponse response = new InventoryStockLogResponse();
        response.setId(stockLog.getId());
        response.setInventoryItemId(stockLog.getInventoryItemId());
        response.setInventoryItemName(stockLog.getInventoryItemName());
        response.setRestaurantId(stockLog.getRestaurantId());
        response.setQuantityChange(stockLog.getQuantityChange());
        response.setBalanceAfter(stockLog.getBalanceAfter());
        response.setType(stockLog.getType());
        response.setReferenceId(stockLog.getReferenceId());
        response.setReferenceType(stockLog.getReferenceType());
        response.setBatchId(stockLog.getBatchId());
        response.setNote(stockLog.getNote());
        response.setCreatedBy(stockLog.getCreatedBy());
        response.setCreatedAt(stockLog.getCreatedAt());
        return response;
    }
}
