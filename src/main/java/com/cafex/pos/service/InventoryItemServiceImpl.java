package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemRequest;
import com.cafex.pos.dto.InventoryItemResponse;
import com.cafex.pos.dto.InventoryItemPageResponse;
import com.cafex.pos.entity.InventoryItem;
import com.cafex.pos.entity.InventoryStockLog;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.InventoryItemRepository;
import com.cafex.pos.repository.InventoryStockLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cafex.pos.exception.ApiException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStockLogRepository inventoryStockLogRepository;

    @Override
    public InventoryItemResponse saveInventoryItem(InventoryItemRequest inventoryItemRequest) {
        log.info("Saving new inventory item");

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setItemId(null);
        inventoryItem.setName(inventoryItemRequest.getName());
        inventoryItem.setDescription(inventoryItemRequest.getDescription());
        inventoryItem.setCategory(inventoryItemRequest.getCategory());
        inventoryItem.setUnitOfMeasure(inventoryItemRequest.getUnitOfMeasure());
        inventoryItem.setCurrentStock(inventoryItemRequest.getCurrentStock());
        inventoryItem.setMinimumStock(inventoryItemRequest.getMinimumStock());
        inventoryItem.setMaximumStock(inventoryItemRequest.getMaximumStock());
        inventoryItem.setUnitCost(inventoryItemRequest.getUnitCost());
        inventoryItem.setSellingPrice(inventoryItemRequest.getSellingPrice());
        inventoryItem.setSupplierId(inventoryItemRequest.getSupplierId());
        inventoryItem.setLocationInStore(inventoryItemRequest.getLocationInStore());
        inventoryItem.setType(inventoryItemRequest.getType() != null ? inventoryItemRequest.getType() : "RAW");
        inventoryItem.setExpiryDate(inventoryItemRequest.getExpiryDate());
        inventoryItem.setRestaurant(new Restaurant());
        inventoryItem.getRestaurant().setId(inventoryItemRequest.getRestaurantId());
        inventoryItem.setCreatedAt(LocalDateTime.now());
        inventoryItem.setCreatedBy(inventoryItemRequest.getCreatedBy());

        InventoryItem savedInventoryItem = inventoryItemRepository.save(inventoryItem);

        savedInventoryItem.setItemId(savedInventoryItem.getId().toString());
        InventoryItem finalInventoryItem = inventoryItemRepository.save(savedInventoryItem);

        log.info("Inventory item saved successfully with ID: {}", finalInventoryItem.getId());
        return convertToResponse(finalInventoryItem);
    }

    @Override
    public InventoryItemResponse updateInventoryItem(Long id, InventoryItemRequest inventoryItemRequest) {
        log.info("Updating inventory item with ID: {}", id);

        InventoryItem existingInventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));

        if (!existingInventoryItem.getItemId().equals(inventoryItemRequest.getItemId()) &&
            inventoryItemRepository.existsByItemId(inventoryItemRequest.getItemId())) {
            throw new ConflictException("Item ID already exists: " + inventoryItemRequest.getItemId());
        }

        java.math.BigDecimal oldStock = existingInventoryItem.getCurrentStock();

        existingInventoryItem.setItemId(inventoryItemRequest.getItemId());
        existingInventoryItem.setName(inventoryItemRequest.getName());
        existingInventoryItem.setDescription(inventoryItemRequest.getDescription());
        existingInventoryItem.setCategory(inventoryItemRequest.getCategory());
        existingInventoryItem.setUnitOfMeasure(inventoryItemRequest.getUnitOfMeasure());
        existingInventoryItem.setCurrentStock(inventoryItemRequest.getCurrentStock());
        existingInventoryItem.setMinimumStock(inventoryItemRequest.getMinimumStock());
        existingInventoryItem.setMaximumStock(inventoryItemRequest.getMaximumStock());
        existingInventoryItem.setUnitCost(inventoryItemRequest.getUnitCost());
        existingInventoryItem.setSellingPrice(inventoryItemRequest.getSellingPrice());
        existingInventoryItem.setSupplierId(inventoryItemRequest.getSupplierId());
        existingInventoryItem.setLocationInStore(inventoryItemRequest.getLocationInStore());
        existingInventoryItem.setType(inventoryItemRequest.getType());
        existingInventoryItem.setExpiryDate(inventoryItemRequest.getExpiryDate());
        if (inventoryItemRequest.getRestaurantId() != null) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(inventoryItemRequest.getRestaurantId());
            existingInventoryItem.setRestaurant(restaurant);
        }
        existingInventoryItem.setUpdatedAt(LocalDateTime.now());
        existingInventoryItem.setUpdatedBy(inventoryItemRequest.getUpdatedBy());

        InventoryItem updatedInventoryItem = inventoryItemRepository.save(existingInventoryItem);
        log.info("Inventory item updated successfully with ID: {}", updatedInventoryItem.getId());

        java.math.BigDecimal newStock = updatedInventoryItem.getCurrentStock();
        if (newStock != null && oldStock != null && newStock.compareTo(oldStock) != 0) {
            java.math.BigDecimal change = newStock.subtract(oldStock);
            InventoryStockLog stockLog = new InventoryStockLog();
            stockLog.setInventoryItemId(updatedInventoryItem.getId());
            stockLog.setInventoryItemName(updatedInventoryItem.getName());
            stockLog.setRestaurantId(updatedInventoryItem.getRestaurant() != null ? updatedInventoryItem.getRestaurant().getId() : null);
            stockLog.setQuantityChange(change);
            stockLog.setBalanceAfter(newStock);
            stockLog.setType("ADJUSTMENT");
            stockLog.setReferenceId(updatedInventoryItem.getId());
            stockLog.setReferenceType("INVENTORY_UPDATE");
            stockLog.setNote("Manual stock update via inventory form");
            stockLog.setCreatedBy(inventoryItemRequest.getUpdatedBy());
            inventoryStockLogRepository.save(stockLog);
            log.info("Stock adjustment logged for inventory item ID: {} (change: {})", updatedInventoryItem.getId(), change);
        }

        return convertToResponse(updatedInventoryItem);
    }

    @Override
    public InventoryItemPageResponse getInventoryItemsWithFilters(String name, String category, String unitOfMeasure, String restaurantId, String isActive, int page, int size) {
        log.info("Fetching inventory items with filters - name: {}, category: {}, unitOfMeasure: {}, restaurantId: {}, isActive: {}, page: {}, size: {}",
                name, category, unitOfMeasure, restaurantId, isActive, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<InventoryItem> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("category"), category));
            }

            if (unitOfMeasure != null && !unitOfMeasure.trim().isEmpty() && !"all".equals(unitOfMeasure)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("unitOfMeasure"), unitOfMeasure));
            }

            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), id));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            if (isActive != null && !isActive.trim().isEmpty() && !"all".equals(isActive)) {
                Boolean active = "true".equals(isActive);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), active));
            }

            return predicate;
        };

        Page<InventoryItem> inventoryItemPage = inventoryItemRepository.findAll(spec, pageable);

        List<InventoryItemResponse> content = inventoryItemPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new InventoryItemPageResponse(
            content,
            inventoryItemPage.getNumber() + 1,
            inventoryItemPage.getTotalPages(),
            inventoryItemPage.getTotalElements()
        );
    }

    @Override
    public Optional<InventoryItemResponse> getInventoryItemById(Long id) {
        log.info("Fetching inventory item by ID: {}", id);
        return inventoryItemRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteInventoryItem(Long id) {
        log.info("Deleting inventory item with ID: {}", id);

        InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));

        inventoryItemRepository.deleteById(id);
        log.info("Inventory item deleted successfully with ID: {}", id);
    }

    private InventoryItemResponse convertToResponse(InventoryItem inventoryItem) {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(inventoryItem.getId());
        response.setItemId(inventoryItem.getItemId());
        response.setName(inventoryItem.getName());
        response.setDescription(inventoryItem.getDescription());
        response.setCategory(inventoryItem.getCategory());
        response.setUnitOfMeasure(inventoryItem.getUnitOfMeasure());
        response.setCurrentStock(inventoryItem.getCurrentStock());
        response.setMinimumStock(inventoryItem.getMinimumStock());
        response.setMaximumStock(inventoryItem.getMaximumStock());
        response.setUnitCost(inventoryItem.getUnitCost());
        response.setSellingPrice(inventoryItem.getSellingPrice());
        response.setSupplierId(inventoryItem.getSupplierId());
        response.setLocationInStore(inventoryItem.getLocationInStore());
        response.setType(inventoryItem.getType());
        response.setExpiryDate(inventoryItem.getExpiryDate());
        response.setLastStockUpdate(inventoryItem.getLastStockUpdate());
        response.setRestaurantId(inventoryItem.getRestaurant() != null ? inventoryItem.getRestaurant().getId() : null);
        response.setCreatedAt(inventoryItem.getCreatedAt());
        response.setUpdatedAt(inventoryItem.getUpdatedAt());
        response.setCreatedBy(inventoryItem.getCreatedBy());
        response.setUpdatedBy(inventoryItem.getUpdatedBy());
        return response;
    }
}
