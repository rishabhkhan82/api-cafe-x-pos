package com.cafex.pos.service;

import com.cafex.pos.dto.WasteTypePageResponse;
import com.cafex.pos.dto.WasteTypeRequest;
import com.cafex.pos.dto.WasteTypeResponse;
import com.cafex.pos.entity.WasteTypeMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.WasteTypeMasterRepository;
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
public class WasteTypeServiceImpl implements WasteTypeService {

    private final WasteTypeMasterRepository wasteTypeMasterRepository;

    @Override
    public WasteTypePageResponse getWasteTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching waste types with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        WasteTypePageResponse allResponse = new WasteTypePageResponse();
        allResponse.setData(getAllWasteTypes());
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(allResponse.getData().size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<WasteTypeMaster> spec = (root, query, criteriaBuilder) -> {
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

        Page<WasteTypeMaster> wastePage = wasteTypeMasterRepository.findAll(spec, pageable);

        List<WasteTypeResponse> content = wastePage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new WasteTypePageResponse(
                content,
                wastePage.getNumber() + 1,
                wastePage.getTotalPages(),
                wastePage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<WasteTypeResponse> getAllWasteTypes() {
        log.info("Fetching all waste types");
        List<WasteTypeMaster> wastes = wasteTypeMasterRepository.findAll();
        return wastes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WasteTypeResponse> getWasteTypeById(Long id) {
        log.info("Fetching waste type by ID: {}", id);
        return wasteTypeMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public WasteTypeResponse createWasteType(WasteTypeRequest request) {
        log.info("Creating new waste type: {}", request.getName());

        if (wasteTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Waste type key already exists: " + request.getKey());
        }

        WasteTypeMaster waste = new WasteTypeMaster();
        waste.setName(request.getName());
        waste.setKey(request.getKey());
        waste.setDescription(request.getDescription());
        waste.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        waste.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        waste.setCreatedBy(request.getCreatedBy());
        waste.setUpdatedBy(request.getUpdatedBy());

        WasteTypeMaster savedWaste = wasteTypeMasterRepository.save(waste);
        log.info("Waste type created successfully with ID: {}", savedWaste.getId());

        return convertToResponse(savedWaste);
    }

    @Override
    public WasteTypeResponse updateWasteType(Long id, WasteTypeRequest request) {
        log.info("Updating waste type with ID: {}", id);

        WasteTypeMaster existingWaste = wasteTypeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste type not found with ID: " + id));

        if (!existingWaste.getKey().equals(request.getKey()) &&
                wasteTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Waste type key already exists: " + request.getKey());
        }

        existingWaste.setName(request.getName());
        existingWaste.setKey(request.getKey());
        existingWaste.setDescription(request.getDescription());
        existingWaste.setIsActive(request.getIsActive());
        existingWaste.setDisplayOrder(request.getDisplayOrder());
        existingWaste.setUpdatedBy(request.getUpdatedBy());
        existingWaste.setUpdatedAt(LocalDateTime.now());

        WasteTypeMaster updatedWaste = wasteTypeMasterRepository.save(existingWaste);
        log.info("Waste type updated successfully with ID: {}", updatedWaste.getId());

        return convertToResponse(updatedWaste);
    }

    @Override
    public void deleteWasteType(Long id) {
        log.info("Deleting waste type with ID: {}", id);

        if (!wasteTypeMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Waste type not found with ID: " + id);
        }

        wasteTypeMasterRepository.deleteById(id);
        log.info("Waste type deleted successfully with ID: {}", id);
    }

    private WasteTypeResponse convertToResponse(WasteTypeMaster waste) {
        WasteTypeResponse response = new WasteTypeResponse();
        response.setId(waste.getId());
        response.setName(waste.getName());
        response.setKey(waste.getKey());
        response.setDescription(waste.getDescription());
        response.setIsActive(waste.getIsActive());
        response.setDisplayOrder(waste.getDisplayOrder());
        response.setCreatedBy(waste.getCreatedBy());
        response.setUpdatedBy(waste.getUpdatedBy());
        response.setCreatedAt(waste.getCreatedAt());
        response.setUpdatedAt(waste.getUpdatedAt());
        return response;
    }
}
