package com.cafex.pos.service;

import com.cafex.pos.dto.InventoryItemUnitPageResponse;
import com.cafex.pos.dto.InventoryItemUnitRequest;
import com.cafex.pos.dto.InventoryItemUnitResponse;
import com.cafex.pos.entity.InventoryItemUnitsMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.InventoryItemUnitsMasterRepository;
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
public class InventoryItemUnitServiceImpl implements InventoryItemUnitService {

    private final InventoryItemUnitsMasterRepository inventoryItemUnitsMasterRepository;

    @Override
    public InventoryItemUnitPageResponse getInventoryItemUnitsWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching inventory item units with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        InventoryItemUnitPageResponse allResponse = new InventoryItemUnitPageResponse();

        Specification<InventoryItemUnitsMaster> spec = (root, query, criteriaBuilder) -> {
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
            List<InventoryItemUnitsMaster> filteredUnits = inventoryItemUnitsMasterRepository.findAll(spec);
            List<InventoryItemUnitResponse> content = filteredUnits.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<InventoryItemUnitsMaster> unitPage = inventoryItemUnitsMasterRepository.findAll(spec, pageable);

        List<InventoryItemUnitResponse> content = unitPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new InventoryItemUnitPageResponse(
                content,
                unitPage.getNumber() + 1,
                unitPage.getTotalPages(),
                unitPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemUnitResponse> getAllInventoryItemUnits() {
        log.info("Fetching all inventory item units");
        List<InventoryItemUnitsMaster> units = inventoryItemUnitsMasterRepository.findAll();
        return units.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryItemUnitResponse> getInventoryItemUnitById(Long id) {
        log.info("Fetching inventory item unit by ID: {}", id);
        return inventoryItemUnitsMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public InventoryItemUnitResponse createInventoryItemUnit(InventoryItemUnitRequest request) {
        log.info("Creating new inventory item unit: {}", request.getName());

        if (inventoryItemUnitsMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Inventory item unit key already exists: " + request.getKey());
        }

        InventoryItemUnitsMaster unit = new InventoryItemUnitsMaster();
        unit.setName(request.getName());
        unit.setKey(request.getKey());
        unit.setDescription(request.getDescription());
        unit.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        unit.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        unit.setCreatedBy(request.getCreatedBy());
        unit.setUpdatedBy(request.getUpdatedBy());

        InventoryItemUnitsMaster savedUnit = inventoryItemUnitsMasterRepository.save(unit);
        log.info("Inventory item unit created successfully with ID: {}", savedUnit.getId());

        return convertToResponse(savedUnit);
    }

    @Override
    public InventoryItemUnitResponse updateInventoryItemUnit(Long id, InventoryItemUnitRequest request) {
        log.info("Updating inventory item unit with ID: {}", id);

        InventoryItemUnitsMaster existingUnit = inventoryItemUnitsMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item unit not found with ID: " + id));

        if (!existingUnit.getKey().equals(request.getKey()) &&
                inventoryItemUnitsMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Inventory item unit key already exists: " + request.getKey());
        }

        existingUnit.setName(request.getName());
        existingUnit.setKey(request.getKey());
        existingUnit.setDescription(request.getDescription());
        existingUnit.setIsActive(request.getIsActive());
        existingUnit.setDisplayOrder(request.getDisplayOrder());
        existingUnit.setUpdatedBy(request.getUpdatedBy());
        existingUnit.setUpdatedAt(LocalDateTime.now());

        InventoryItemUnitsMaster updatedUnit = inventoryItemUnitsMasterRepository.save(existingUnit);
        log.info("Inventory item unit updated successfully with ID: {}", updatedUnit.getId());

        return convertToResponse(updatedUnit);
    }

    @Override
    public void deleteInventoryItemUnit(Long id) {
        log.info("Deleting inventory item unit with ID: {}", id);

        if (!inventoryItemUnitsMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory item unit not found with ID: " + id);
        }

        inventoryItemUnitsMasterRepository.deleteById(id);
        log.info("Inventory item unit deleted successfully with ID: {}", id);
    }

    private InventoryItemUnitResponse convertToResponse(InventoryItemUnitsMaster unit) {
        InventoryItemUnitResponse response = new InventoryItemUnitResponse();
        response.setId(unit.getId());
        response.setName(unit.getName());
        response.setKey(unit.getKey());
        response.setDescription(unit.getDescription());
        response.setIsActive(unit.getIsActive());
        response.setDisplayOrder(unit.getDisplayOrder());
        response.setCreatedBy(unit.getCreatedBy());
        response.setUpdatedBy(unit.getUpdatedBy());
        response.setCreatedAt(unit.getCreatedAt());
        response.setUpdatedAt(unit.getUpdatedAt());
        return response;
    }
}
