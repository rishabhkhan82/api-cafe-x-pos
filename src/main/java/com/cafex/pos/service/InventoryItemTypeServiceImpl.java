package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemTypePageResponse;
import com.cafex.pos.dto.InventoryItemTypeRequest;
import com.cafex.pos.dto.InventoryItemTypeResponse;
import com.cafex.pos.entity.InventoryItemTypesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.InventoryItemTypesMasterRepository;
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
public class InventoryItemTypeServiceImpl implements InventoryItemTypeService {

    private final InventoryItemTypesMasterRepository inventoryItemTypesMasterRepository;

    @Override
    public InventoryItemTypePageResponse getInventoryItemTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching inventory item types with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        InventoryItemTypePageResponse allResponse = new InventoryItemTypePageResponse();
        allResponse.setData(getAllInventoryItemTypes());
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(allResponse.getData().size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<InventoryItemTypesMaster> spec = (root, query, criteriaBuilder) -> {
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

        Page<InventoryItemTypesMaster> typePage = inventoryItemTypesMasterRepository.findAll(spec, pageable);

        List<InventoryItemTypeResponse> content = typePage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new InventoryItemTypePageResponse(
                content,
                typePage.getNumber() + 1,
                typePage.getTotalPages(),
                typePage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemTypeResponse> getAllInventoryItemTypes() {
        log.info("Fetching all inventory item types");
        List<InventoryItemTypesMaster> types = inventoryItemTypesMasterRepository.findAll();
        return types.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryItemTypeResponse> getInventoryItemTypeById(Long id) {
        log.info("Fetching inventory item type by ID: {}", id);
        return inventoryItemTypesMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public InventoryItemTypeResponse createInventoryItemType(InventoryItemTypeRequest request) {
        log.info("Creating new inventory item type: {}", request.getName());

        if (inventoryItemTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Inventory item type key already exists: " + request.getKey());
        }

        InventoryItemTypesMaster type = new InventoryItemTypesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());

        InventoryItemTypesMaster savedType = inventoryItemTypesMasterRepository.save(type);
        log.info("Inventory item type created successfully with ID: {}", savedType.getId());

        return convertToResponse(savedType);
    }

    @Override
    public InventoryItemTypeResponse updateInventoryItemType(Long id, InventoryItemTypeRequest request) {
        log.info("Updating inventory item type with ID: {}", id);

        InventoryItemTypesMaster existingType = inventoryItemTypesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item type not found with ID: " + id));

        if (!existingType.getKey().equals(request.getKey()) &&
                inventoryItemTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Inventory item type key already exists: " + request.getKey());
        }

        existingType.setName(request.getName());
        existingType.setKey(request.getKey());
        existingType.setDescription(request.getDescription());
        existingType.setIsActive(request.getIsActive());
        existingType.setDisplayOrder(request.getDisplayOrder());
        existingType.setUpdatedBy(request.getUpdatedBy());
        existingType.setUpdatedAt(LocalDateTime.now());

        InventoryItemTypesMaster updatedType = inventoryItemTypesMasterRepository.save(existingType);
        log.info("Inventory item type updated successfully with ID: {}", updatedType.getId());

        return convertToResponse(updatedType);
    }

    @Override
    public void deleteInventoryItemType(Long id) {
        log.info("Deleting inventory item type with ID: {}", id);

        if (!inventoryItemTypesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory item type not found with ID: " + id);
        }

        inventoryItemTypesMasterRepository.deleteById(id);
        log.info("Inventory item type deleted successfully with ID: {}", id);
    }

    private InventoryItemTypeResponse convertToResponse(InventoryItemTypesMaster type) {
        InventoryItemTypeResponse response = new InventoryItemTypeResponse();
        response.setId(type.getId());
        response.setName(type.getName());
        response.setKey(type.getKey());
        response.setDescription(type.getDescription());
        response.setIsActive(type.getIsActive());
        response.setDisplayOrder(type.getDisplayOrder());
        response.setCreatedBy(type.getCreatedBy());
        response.setUpdatedBy(type.getUpdatedBy());
        response.setCreatedAt(type.getCreatedAt());
        response.setUpdatedAt(type.getUpdatedAt());
        return response;
    }
}
