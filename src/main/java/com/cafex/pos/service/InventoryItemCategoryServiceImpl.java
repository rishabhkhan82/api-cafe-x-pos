package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemCategoryPageResponse;
import com.cafex.pos.dto.InventoryItemCategoryRequest;
import com.cafex.pos.dto.InventoryItemCategoryResponse;
import com.cafex.pos.entity.InventoryItemCategoriesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.InventoryItemCategoriesMasterRepository;
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
public class InventoryItemCategoryServiceImpl implements InventoryItemCategoryService {

    private final InventoryItemCategoriesMasterRepository inventoryItemCategoriesMasterRepository;

    @Override
    public InventoryItemCategoryPageResponse getInventoryItemCategoriesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching inventory item categories with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        InventoryItemCategoryPageResponse allResponse = new InventoryItemCategoryPageResponse();
        allResponse.setData(getAllInventoryItemCategories());
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(allResponse.getData().size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<InventoryItemCategoriesMaster> spec = (root, query, criteriaBuilder) -> {
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

        Page<InventoryItemCategoriesMaster> categoryPage = inventoryItemCategoriesMasterRepository.findAll(spec, pageable);

        List<InventoryItemCategoryResponse> content = categoryPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new InventoryItemCategoryPageResponse(
                content,
                categoryPage.getNumber() + 1,
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemCategoryResponse> getAllInventoryItemCategories() {
        log.info("Fetching all inventory item categories");
        List<InventoryItemCategoriesMaster> categories = inventoryItemCategoriesMasterRepository.findAll();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryItemCategoryResponse> getInventoryItemCategoryById(Long id) {
        log.info("Fetching inventory item category by ID: {}", id);
        return inventoryItemCategoriesMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public InventoryItemCategoryResponse createInventoryItemCategory(InventoryItemCategoryRequest request) {
        log.info("Creating new inventory item category: {}", request.getName());

        if (inventoryItemCategoriesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Inventory item category key already exists: " + request.getKey());
        }

        InventoryItemCategoriesMaster category = new InventoryItemCategoriesMaster();
        category.setName(request.getName());
        category.setKey(request.getKey());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setCreatedBy(request.getCreatedBy());
        category.setUpdatedBy(request.getUpdatedBy());

        InventoryItemCategoriesMaster savedCategory = inventoryItemCategoriesMasterRepository.save(category);
        log.info("Inventory item category created successfully with ID: {}", savedCategory.getId());

        return convertToResponse(savedCategory);
    }

    @Override
    public InventoryItemCategoryResponse updateInventoryItemCategory(Long id, InventoryItemCategoryRequest request) {
        log.info("Updating inventory item category with ID: {}", id);

        InventoryItemCategoriesMaster existingCategory = inventoryItemCategoriesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item category not found with ID: " + id));

        if (!existingCategory.getKey().equals(request.getKey()) &&
                inventoryItemCategoriesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Inventory item category key already exists: " + request.getKey());
        }

        existingCategory.setName(request.getName());
        existingCategory.setKey(request.getKey());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setIsActive(request.getIsActive());
        existingCategory.setDisplayOrder(request.getDisplayOrder());
        existingCategory.setUpdatedBy(request.getUpdatedBy());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        InventoryItemCategoriesMaster updatedCategory = inventoryItemCategoriesMasterRepository.save(existingCategory);
        log.info("Inventory item category updated successfully with ID: {}", updatedCategory.getId());

        return convertToResponse(updatedCategory);
    }

    @Override
    public void deleteInventoryItemCategory(Long id) {
        log.info("Deleting inventory item category with ID: {}", id);

        if (!inventoryItemCategoriesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory item category not found with ID: " + id);
        }

        inventoryItemCategoriesMasterRepository.deleteById(id);
        log.info("Inventory item category deleted successfully with ID: {}", id);
    }

    private InventoryItemCategoryResponse convertToResponse(InventoryItemCategoriesMaster category) {
        InventoryItemCategoryResponse response = new InventoryItemCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setKey(category.getKey());
        response.setDescription(category.getDescription());
        response.setIsActive(category.getIsActive());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setCreatedBy(category.getCreatedBy());
        response.setUpdatedBy(category.getUpdatedBy());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
