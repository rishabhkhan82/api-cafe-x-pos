package com.cafex.pos.service;

import com.cafex.pos.dto.WasteReasonTypePageResponse;
import com.cafex.pos.dto.WasteReasonTypeRequest;
import com.cafex.pos.dto.WasteReasonTypeResponse;
import com.cafex.pos.entity.WasteReasonTypeMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.WasteReasonTypeMasterRepository;
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
public class WasteReasonTypeServiceImpl implements WasteReasonTypeService {

    private final WasteReasonTypeMasterRepository wasteReasonTypeMasterRepository;

    @Override
    public WasteReasonTypePageResponse getWasteReasonTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching waste reason types with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        WasteReasonTypePageResponse allResponse = new WasteReasonTypePageResponse();
        allResponse.setData(getAllWasteReasonTypes());
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(allResponse.getData().size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<WasteReasonTypeMaster> spec = (root, query, criteriaBuilder) -> {
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

        Page<WasteReasonTypeMaster> reasonPage = wasteReasonTypeMasterRepository.findAll(spec, pageable);

        List<WasteReasonTypeResponse> content = reasonPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new WasteReasonTypePageResponse(
                content,
                reasonPage.getNumber() + 1,
                reasonPage.getTotalPages(),
                reasonPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<WasteReasonTypeResponse> getAllWasteReasonTypes() {
        log.info("Fetching all waste reason types");
        List<WasteReasonTypeMaster> reasons = wasteReasonTypeMasterRepository.findAll();
        return reasons.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WasteReasonTypeResponse> getWasteReasonTypeById(Long id) {
        log.info("Fetching waste reason type by ID: {}", id);
        return wasteReasonTypeMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public WasteReasonTypeResponse createWasteReasonType(WasteReasonTypeRequest request) {
        log.info("Creating new waste reason type: {}", request.getName());

        if (wasteReasonTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Waste reason type key already exists: " + request.getKey());
        }

        WasteReasonTypeMaster reason = new WasteReasonTypeMaster();
        reason.setName(request.getName());
        reason.setKey(request.getKey());
        reason.setDescription(request.getDescription());
        reason.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        reason.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        reason.setCreatedBy(request.getCreatedBy());
        reason.setUpdatedBy(request.getUpdatedBy());

        WasteReasonTypeMaster savedReason = wasteReasonTypeMasterRepository.save(reason);
        log.info("Waste reason type created successfully with ID: {}", savedReason.getId());

        return convertToResponse(savedReason);
    }

    @Override
    public WasteReasonTypeResponse updateWasteReasonType(Long id, WasteReasonTypeRequest request) {
        log.info("Updating waste reason type with ID: {}", id);

        WasteReasonTypeMaster existingReason = wasteReasonTypeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste reason type not found with ID: " + id));

        if (!existingReason.getKey().equals(request.getKey()) &&
                wasteReasonTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Waste reason type key already exists: " + request.getKey());
        }

        existingReason.setName(request.getName());
        existingReason.setKey(request.getKey());
        existingReason.setDescription(request.getDescription());
        existingReason.setIsActive(request.getIsActive());
        existingReason.setDisplayOrder(request.getDisplayOrder());
        existingReason.setUpdatedBy(request.getUpdatedBy());
        existingReason.setUpdatedAt(LocalDateTime.now());

        WasteReasonTypeMaster updatedReason = wasteReasonTypeMasterRepository.save(existingReason);
        log.info("Waste reason type updated successfully with ID: {}", updatedReason.getId());

        return convertToResponse(updatedReason);
    }

    @Override
    public void deleteWasteReasonType(Long id) {
        log.info("Deleting waste reason type with ID: {}", id);

        if (!wasteReasonTypeMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Waste reason type not found with ID: " + id);
        }

        wasteReasonTypeMasterRepository.deleteById(id);
        log.info("Waste reason type deleted successfully with ID: {}", id);
    }

    private WasteReasonTypeResponse convertToResponse(WasteReasonTypeMaster reason) {
        WasteReasonTypeResponse response = new WasteReasonTypeResponse();
        response.setId(reason.getId());
        response.setName(reason.getName());
        response.setKey(reason.getKey());
        response.setDescription(reason.getDescription());
        response.setIsActive(reason.getIsActive());
        response.setDisplayOrder(reason.getDisplayOrder());
        response.setCreatedBy(reason.getCreatedBy());
        response.setUpdatedBy(reason.getUpdatedBy());
        response.setCreatedAt(reason.getCreatedAt());
        response.setUpdatedAt(reason.getUpdatedAt());
        return response;
    }
}
