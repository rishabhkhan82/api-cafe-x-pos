package com.cafex.pos.service;

import com.cafex.pos.dto.FeatureCategoryPageResponse;
import com.cafex.pos.dto.FeatureCategoryRequest;
import com.cafex.pos.dto.FeatureCategoryResponse;
import com.cafex.pos.entity.FeatureCategoriesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.FeatureCategoriesMasterRepository;
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
public class FeatureCategoryServiceImpl implements FeatureCategoryService {

    private final FeatureCategoriesMasterRepository featureCategoriesMasterRepository;

    @Override
    public FeatureCategoryPageResponse getFeatureCategoriesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching feature categories with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        FeatureCategoryPageResponse allResponse = new FeatureCategoryPageResponse();
        allResponse.setData(getAllFeatureCategories());
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(allResponse.getData().size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<FeatureCategoriesMaster> spec = (root, query, criteriaBuilder) -> {
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

        Page<FeatureCategoriesMaster> categoryPage = featureCategoriesMasterRepository.findAll(spec, pageable);

        List<FeatureCategoryResponse> content = categoryPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new FeatureCategoryPageResponse(
                content,
                categoryPage.getNumber() + 1,
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeatureCategoryResponse> getAllFeatureCategories() {
        log.info("Fetching all feature categories");
        List<FeatureCategoriesMaster> categories = featureCategoriesMasterRepository.findAll();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeatureCategoryResponse> getFeatureCategoryById(Long id) {
        log.info("Fetching feature category by ID: {}", id);
        return featureCategoriesMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public FeatureCategoryResponse createFeatureCategory(FeatureCategoryRequest request) {
        log.info("Creating new feature category: {}", request.getName());

        if (featureCategoriesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Feature category key already exists: " + request.getKey());
        }

        FeatureCategoriesMaster category = new FeatureCategoriesMaster();
        category.setName(request.getName());
        category.setKey(request.getKey());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setCreatedBy(request.getCreatedBy());
        category.setUpdatedBy(request.getUpdatedBy());

        FeatureCategoriesMaster savedCategory = featureCategoriesMasterRepository.save(category);
        log.info("Feature category created successfully with ID: {}", savedCategory.getId());

        return convertToResponse(savedCategory);
    }

    @Override
    public FeatureCategoryResponse updateFeatureCategory(Long id, FeatureCategoryRequest request) {
        log.info("Updating feature category with ID: {}", id);

        FeatureCategoriesMaster existingCategory = featureCategoriesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature category not found with ID: " + id));

        if (!existingCategory.getKey().equals(request.getKey()) &&
                featureCategoriesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Feature category key already exists: " + request.getKey());
        }

        existingCategory.setName(request.getName());
        existingCategory.setKey(request.getKey());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setIsActive(request.getIsActive());
        existingCategory.setDisplayOrder(request.getDisplayOrder());
        existingCategory.setUpdatedBy(request.getUpdatedBy());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        FeatureCategoriesMaster updatedCategory = featureCategoriesMasterRepository.save(existingCategory);
        log.info("Feature category updated successfully with ID: {}", updatedCategory.getId());

        return convertToResponse(updatedCategory);
    }

    @Override
    public void deleteFeatureCategory(Long id) {
        log.info("Deleting feature category with ID: {}", id);

        if (!featureCategoriesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feature category not found with ID: " + id);
        }

        featureCategoriesMasterRepository.deleteById(id);
        log.info("Feature category deleted successfully with ID: {}", id);
    }

    private FeatureCategoryResponse convertToResponse(FeatureCategoriesMaster category) {
        FeatureCategoryResponse response = new FeatureCategoryResponse();
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
