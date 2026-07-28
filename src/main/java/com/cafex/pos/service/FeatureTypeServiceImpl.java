package com.cafex.pos.service;

import com.cafex.pos.dto.FeatureTypePageResponse;
import com.cafex.pos.dto.FeatureTypeRequest;
import com.cafex.pos.dto.FeatureTypeResponse;
import com.cafex.pos.entity.FeatureTypesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.FeatureTypesMasterRepository;
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
public class FeatureTypeServiceImpl implements FeatureTypeService {

    private final FeatureTypesMasterRepository featureTypesMasterRepository;

    @Override
    public FeatureTypePageResponse getFeatureTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching feature types with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);

        FeatureTypePageResponse allResponse = new FeatureTypePageResponse();

        Specification<FeatureTypesMaster> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
            }
            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), isActive));
            }
            return predicate;
        };

        if (page == 0 && size == 0) {
            List<FeatureTypesMaster> filteredTypes = featureTypesMasterRepository.findAll(spec);
            List<FeatureTypeResponse> content = filteredTypes.stream().map(this::convertToResponse).collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<FeatureTypesMaster> typePage = featureTypesMasterRepository.findAll(spec, pageable);
        List<FeatureTypeResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new FeatureTypePageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeatureTypeResponse> getAllFeatureTypes() {
        log.info("Fetching all feature types");
        return featureTypesMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeatureTypeResponse> getFeatureTypeById(Long id) {
        log.info("Fetching feature type by ID: {}", id);
        return featureTypesMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public FeatureTypeResponse createFeatureType(FeatureTypeRequest request) {
        log.info("Creating new feature type: {}", request.getName());
        if (featureTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Feature type key already exists: " + request.getKey());
        }
        FeatureTypesMaster type = new FeatureTypesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        FeatureTypesMaster saved = featureTypesMasterRepository.save(type);
        log.info("Feature type created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public FeatureTypeResponse updateFeatureType(Long id, FeatureTypeRequest request) {
        log.info("Updating feature type with ID: {}", id);
        FeatureTypesMaster existing = featureTypesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature type not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && featureTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Feature type key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        FeatureTypesMaster updated = featureTypesMasterRepository.save(existing);
        log.info("Feature type updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteFeatureType(Long id) {
        log.info("Deleting feature type with ID: {}", id);
        if (!featureTypesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feature type not found with ID: " + id);
        }
        featureTypesMasterRepository.deleteById(id);
        log.info("Feature type deleted successfully with ID: {}", id);
    }

    private FeatureTypeResponse convertToResponse(FeatureTypesMaster type) {
        FeatureTypeResponse response = new FeatureTypeResponse();
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
