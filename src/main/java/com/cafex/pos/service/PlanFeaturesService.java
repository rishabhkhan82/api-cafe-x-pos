package com.cafex.pos.service;

import com.cafex.pos.dto.PlanFeaturesRequest;
import com.cafex.pos.dto.PlanFeaturesResponse;
import com.cafex.pos.dto.PlanFeaturesPageResponse;
import com.cafex.pos.entity.PlanFeatures;
import com.cafex.pos.repository.PlanFeaturesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlanFeaturesService {

    private final PlanFeaturesRepository planFeaturesRepository;

    public PlanFeaturesPageResponse getPlanFeaturesWithFilters(String name, String category, Boolean isEnabled, String featureType, int page, int size) {
        log.info("Fetching plan features with filters - name: {}, category: {}, isEnabled: {}, featureType: {}, page: {}, size: {}",
                name, category, isEnabled, featureType, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<PlanFeatures> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
            }

            // Category filter
            if (category != null && !category.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("category"), category));
            }

            // IsEnabled filter
            if (isEnabled != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("isEnabled"), isEnabled));
            }

            // FeatureType filter
            if (featureType != null && !featureType.trim().isEmpty()) {
                try {
                    PlanFeatures.FeatureType type = PlanFeatures.FeatureType.valueOf(featureType.toUpperCase());
                    predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("featureType"), type));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid feature type provided: {}", featureType);
                }
            }

            return predicate;
        };

        Page<PlanFeatures> featuresPage = planFeaturesRepository.findAll(spec, pageable);

        List<PlanFeaturesResponse> content = featuresPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PlanFeaturesPageResponse(
            content,
            featuresPage.getNumber() + 1, // currentPage (1-based)
            featuresPage.getTotalPages(),
            featuresPage.getTotalElements()
        );
    }

    public Optional<PlanFeaturesResponse> getPlanFeatureById(Long id) {
        log.info("Fetching plan feature by ID: {}", id);
        return planFeaturesRepository.findById(id)
                .map(this::convertToResponse);
    }

    public List<PlanFeaturesResponse> getAllPlanFeatures() {
        log.info("Fetching all plan features");
        List<PlanFeatures> features = planFeaturesRepository.findAll();
        return features.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PlanFeaturesResponse savePlanFeature(PlanFeaturesRequest planFeaturesRequest) {
        log.info("Saving new plan feature: {}", planFeaturesRequest.getName());

        // Check if featureId already exists
        if (planFeaturesRepository.existsByFeatureId(planFeaturesRequest.getFeatureId())) {
            throw new RuntimeException("Feature ID already exists: " + planFeaturesRequest.getFeatureId());
        }

        PlanFeatures planFeature = new PlanFeatures();
        planFeature.setFeatureId(planFeaturesRequest.getFeatureId());
        planFeature.setName(planFeaturesRequest.getName());
        planFeature.setDescription(planFeaturesRequest.getDescription());
        planFeature.setCategory(planFeaturesRequest.getCategory());
        planFeature.setCategoryIcon(planFeaturesRequest.getCategoryIcon());
        planFeature.setFeatureType(planFeaturesRequest.getFeatureType());
        planFeature.setIsEnabled(planFeaturesRequest.getIsEnabled() != null ? planFeaturesRequest.getIsEnabled() : true);
        planFeature.setSortOrder(planFeaturesRequest.getSortOrder() != null ? planFeaturesRequest.getSortOrder() : 0);
        planFeature.setCreatedBy(planFeaturesRequest.getCreatedBy());
        planFeature.setUpdatedBy(planFeaturesRequest.getUpdatedBy());
        planFeature.setCreatedAt(LocalDateTime.now());
        planFeature.setUpdatedAt(LocalDateTime.now());

        PlanFeatures savedFeature = planFeaturesRepository.save(planFeature);
        log.info("Plan feature saved successfully with ID: {}", savedFeature.getId());

        return convertToResponse(savedFeature);
    }

    public PlanFeaturesResponse updatePlanFeature(Long id, PlanFeaturesRequest planFeaturesRequest) {
        log.info("Updating plan feature with ID: {}", id);

        PlanFeatures existingFeature = planFeaturesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan feature not found with ID: " + id));

        // Check featureId uniqueness if changed
        if (!existingFeature.getFeatureId().equals(planFeaturesRequest.getFeatureId()) &&
            planFeaturesRepository.existsByFeatureId(planFeaturesRequest.getFeatureId())) {
            throw new RuntimeException("Feature ID already exists: " + planFeaturesRequest.getFeatureId());
        }

        // Update fields
        existingFeature.setFeatureId(planFeaturesRequest.getFeatureId());
        existingFeature.setName(planFeaturesRequest.getName());
        existingFeature.setDescription(planFeaturesRequest.getDescription());
        existingFeature.setCategory(planFeaturesRequest.getCategory());
        existingFeature.setCategoryIcon(planFeaturesRequest.getCategoryIcon());
        existingFeature.setFeatureType(planFeaturesRequest.getFeatureType());
        existingFeature.setIsEnabled(planFeaturesRequest.getIsEnabled());
        existingFeature.setSortOrder(planFeaturesRequest.getSortOrder());
        existingFeature.setUpdatedBy(planFeaturesRequest.getUpdatedBy());
        existingFeature.setUpdatedAt(LocalDateTime.now());

        PlanFeatures updatedFeature = planFeaturesRepository.save(existingFeature);
        log.info("Plan feature updated successfully with ID: {}", updatedFeature.getId());

        return convertToResponse(updatedFeature);
    }

    public void deletePlanFeature(Long id) {
        log.info("Deleting plan feature with ID: {}", id);

        if (!planFeaturesRepository.existsById(id)) {
            throw new RuntimeException("Plan feature not found with ID: " + id);
        }

        planFeaturesRepository.deleteById(id);
        log.info("Plan feature deleted successfully with ID: {}", id);
    }

    private PlanFeaturesResponse convertToResponse(PlanFeatures planFeature) {
        PlanFeaturesResponse response = new PlanFeaturesResponse();
        response.setId(planFeature.getId());
        response.setFeatureId(planFeature.getFeatureId());
        response.setName(planFeature.getName());
        response.setDescription(planFeature.getDescription());
        response.setCategory(planFeature.getCategory());
        response.setCategoryIcon(planFeature.getCategoryIcon());
        response.setFeatureType(planFeature.getFeatureType());
        response.setIsEnabled(planFeature.getIsEnabled());
        response.setSortOrder(planFeature.getSortOrder());
        response.setCreatedBy(planFeature.getCreatedBy());
        response.setUpdatedBy(planFeature.getUpdatedBy());
        response.setCreatedAt(planFeature.getCreatedAt());
        response.setUpdatedAt(planFeature.getUpdatedAt());
        return response;
    }
}