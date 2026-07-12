package com.cafex.pos.service;

import com.cafex.pos.dto.WasteManagementRequest;
import com.cafex.pos.dto.WasteManagementResponse;
import com.cafex.pos.entity.InventoryItem;
import com.cafex.pos.entity.InventoryStockLog;
import com.cafex.pos.entity.WasteManagement;
import com.cafex.pos.repository.InventoryItemRepository;
import com.cafex.pos.repository.InventoryStockLogRepository;
import com.cafex.pos.repository.RecipeRepository;
import com.cafex.pos.repository.WasteManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WasteManagementServiceImpl implements WasteManagementService {

    private final WasteManagementRepository wasteManagementRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStockLogRepository inventoryStockLogRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public List<WasteManagementResponse> createWasteBatch(List<WasteManagementRequest> requests) {
        log.info("Creating waste batch with {} entries", requests.size());
        List<WasteManagementResponse> responses = new ArrayList<>();

        for (WasteManagementRequest request : requests) {
            WasteManagement waste = new WasteManagement();
            waste.setRestaurantId(request.getRestaurantId());
            waste.setInventoryItemId(request.getInventoryItemId());
            waste.setItemName(request.getItemName());
            waste.setQuantity(request.getQuantity());
            waste.setReason(request.getReason());
            waste.setNote(request.getNote());
            waste.setWasteDate(request.getWasteDate() != null ? request.getWasteDate() : LocalDateTime.now());
            waste.setCreatedBy(request.getCreatedBy());
            waste.setWasteType(request.getWasteType());
            waste.setRecipeId(request.getRecipeId());
            waste.setWasteCost(request.getWasteCost());

            WasteManagement saved = wasteManagementRepository.save(waste);
            responses.add(convertToResponse(saved));

            // Deduct stock if linked to inventory item
            if (request.getInventoryItemId() != null) {
                InventoryItem item = inventoryItemRepository.findById(request.getInventoryItemId())
                        .orElse(null);
                if (item != null) {
                    BigDecimal newStock = item.getCurrentStock().subtract(request.getQuantity());
                    item.setCurrentStock(newStock);
                    inventoryItemRepository.save(item);

                    // Create stock log
                    InventoryStockLog log = new InventoryStockLog();
                    log.setInventoryItemId(request.getInventoryItemId());
                    log.setInventoryItemName(item != null ? item.getName() : null);
                    log.setRestaurantId(request.getRestaurantId());
                    log.setQuantityChange(request.getQuantity().negate());
                    log.setBalanceAfter(newStock);
                    log.setType("WASTAGE");
                    log.setReferenceId(saved.getId());
                    log.setReferenceType("WASTE");
                    log.setNote(request.getReason() + (request.getNote() != null ? ": " + request.getNote() : ""));
                    log.setCreatedBy(request.getCreatedBy());
                    inventoryStockLogRepository.save(log);
                }
            }
        }

        log.info("Waste batch created with {} entries", responses.size());
        return responses;
    }

    @Override
    public Map<String, Object> getWasteByRestaurant(Long restaurantId, Pageable pageable, String search, String wasteType, String reason) {
        log.info("Fetching waste entries for restaurant: {}, search: {}, type: {}, reason: {}", restaurantId, search, wasteType, reason);
        Page<WasteManagement> page = wasteManagementRepository.findAll((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.equal(root.get("restaurantId"), restaurantId)
            );

            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("itemName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), searchPattern)
                    )
                );
            }

            if (wasteType != null && !wasteType.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("wasteType"), wasteType));
            }

            if (reason != null && !reason.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("reason"), reason));
            }

            return predicate;
        }, pageable);

        List<WasteManagementResponse> data = page.getContent().stream()
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
    public Optional<WasteManagementResponse> getWasteById(Long id) {
        return wasteManagementRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteWaste(Long id) {
        wasteManagementRepository.deleteById(id);
    }

    private WasteManagementResponse convertToResponse(WasteManagement waste) {
        WasteManagementResponse response = new WasteManagementResponse();
        response.setId(waste.getId());
        response.setRestaurantId(waste.getRestaurantId());
        response.setInventoryItemId(waste.getInventoryItemId());
        response.setInventoryItemName(fetchInventoryItemName(waste.getInventoryItemId()));
        response.setItemName(waste.getItemName());
        response.setQuantity(waste.getQuantity());
        response.setReason(waste.getReason());
        response.setNote(waste.getNote());
        response.setWasteDate(waste.getWasteDate());
        response.setCreatedBy(waste.getCreatedBy());
        response.setCreatedAt(waste.getCreatedAt());
        response.setWasteType(waste.getWasteType());
        response.setRecipeId(waste.getRecipeId());
        response.setRecipeName(fetchRecipeName(waste.getRecipeId()));
        response.setWasteCost(waste.getWasteCost());
        return response;
    }

    private String fetchInventoryItemName(Long inventoryItemId) {
        if (inventoryItemId == null) return null;
        return inventoryItemRepository.findById(inventoryItemId)
                .map(item -> item.getName())
                .orElse(null);
    }

    private String fetchRecipeName(Long recipeId) {
        if (recipeId == null) return null;
        return recipeRepository.findById(recipeId)
                .map(recipe -> recipe.getName())
                .orElse(null);
    }
}
