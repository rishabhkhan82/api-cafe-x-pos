package com.cafex.pos.service;

import com.cafex.pos.dto.PlanFeaturesMappingRequest;
import com.cafex.pos.dto.PlanFeaturesMappingResponse;
import com.cafex.pos.dto.PlanFeaturesMappingPageResponse;
import com.cafex.pos.entity.PlanFeaturesMapping;
import com.cafex.pos.entity.SubscriptionPlans;
import com.cafex.pos.repository.PlanFeaturesMappingRepository;
import com.cafex.pos.repository.SubscriptionPlansRepository;
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
public class PlanFeaturesMappingService {

    private final PlanFeaturesMappingRepository planFeaturesMappingRepository;
    private final SubscriptionPlansRepository subscriptionPlansRepository;

    public PlanFeaturesMappingPageResponse getPlanFeaturesMappingsWithFilters(Long planId, String featureId, Boolean isEnabled, int page, int size) {
        log.info("Fetching plan features mappings with filters - planId: {}, featureId: {}, isEnabled: {}, page: {}, size: {}",
                planId, featureId, isEnabled, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<PlanFeaturesMapping> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Plan ID filter
            if (planId != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("plan").get("id"), planId));
            }

            // Feature ID filter
            if (featureId != null && !featureId.trim().isEmpty()) {
                String searchTerm = "%" + featureId.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("featureId")), searchTerm));
            }

            // IsEnabled filter
            if (isEnabled != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("isEnabled"), isEnabled));
            }

            return predicate;
        };

        Page<PlanFeaturesMapping> mappingPage = planFeaturesMappingRepository.findAll(spec, pageable);

        List<PlanFeaturesMappingResponse> content = mappingPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PlanFeaturesMappingPageResponse(
            content,
            mappingPage.getNumber() + 1, // currentPage (1-based)
            mappingPage.getTotalPages(),
            mappingPage.getTotalElements()
        );
    }

    public Optional<PlanFeaturesMappingResponse> getPlanFeaturesMappingById(Long id) {
        log.info("Fetching plan features mapping by ID: {}", id);
        return planFeaturesMappingRepository.findById(id)
                .map(this::convertToResponse);
    }

    public List<PlanFeaturesMappingResponse> getAllPlanFeaturesMappings() {
        log.info("Fetching all plan features mappings");
        List<PlanFeaturesMapping> mappings = planFeaturesMappingRepository.findAll();
        return mappings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PlanFeaturesMappingResponse savePlanFeaturesMapping(PlanFeaturesMappingRequest planFeaturesMappingRequest) {
        log.info("Saving new plan features mapping for planId: {}", planFeaturesMappingRequest.getPlanId());

        // Check if plan exists
        SubscriptionPlans plan = subscriptionPlansRepository.findById(planFeaturesMappingRequest.getPlanId())
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with ID: " + planFeaturesMappingRequest.getPlanId()));

        // Check for duplicate plan-feature combination
        if (planFeaturesMappingRepository.existsByPlanIdAndFeatureId(planFeaturesMappingRequest.getPlanId(), planFeaturesMappingRequest.getFeatureId())) {
            throw new RuntimeException("Plan-feature mapping already exists for planId: " + planFeaturesMappingRequest.getPlanId() + " and featureId: " + planFeaturesMappingRequest.getFeatureId());
        }

        PlanFeaturesMapping mapping = new PlanFeaturesMapping();
        mapping.setPlan(plan);
        mapping.setFeatureId(planFeaturesMappingRequest.getFeatureId());
        mapping.setIsEnabled(planFeaturesMappingRequest.getIsEnabled() != null ? planFeaturesMappingRequest.getIsEnabled() : true);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());

        PlanFeaturesMapping savedMapping = planFeaturesMappingRepository.save(mapping);
        log.info("Plan features mapping saved successfully with ID: {}", savedMapping.getId());

        return convertToResponse(savedMapping);
    }

    public PlanFeaturesMappingResponse updatePlanFeaturesMapping(Long id, PlanFeaturesMappingRequest planFeaturesMappingRequest) {
        log.info("Updating plan features mapping with ID: {}", id);

        PlanFeaturesMapping existingMapping = planFeaturesMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan features mapping not found with ID: " + id));

        // Update fields only if provided
        if (planFeaturesMappingRequest.getPlanId() != null) {
            // Check if plan exists
            SubscriptionPlans plan = subscriptionPlansRepository.findById(planFeaturesMappingRequest.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found with ID: " + planFeaturesMappingRequest.getPlanId()));
            existingMapping.setPlan(plan);
        }

        if (planFeaturesMappingRequest.getFeatureId() != null && !planFeaturesMappingRequest.getFeatureId().trim().isEmpty()) {
            existingMapping.setFeatureId(planFeaturesMappingRequest.getFeatureId());
        }

        // Check for duplicate if plan or feature changed
        if (planFeaturesMappingRequest.getPlanId() != null && planFeaturesMappingRequest.getFeatureId() != null &&
            !planFeaturesMappingRequest.getFeatureId().trim().isEmpty()) {
            if (!existingMapping.getPlan().getId().equals(planFeaturesMappingRequest.getPlanId()) ||
                !existingMapping.getFeatureId().equals(planFeaturesMappingRequest.getFeatureId())) {
                if (planFeaturesMappingRepository.existsByPlanIdAndFeatureId(planFeaturesMappingRequest.getPlanId(), planFeaturesMappingRequest.getFeatureId())) {
                    throw new RuntimeException("Plan-feature mapping already exists for planId: " + planFeaturesMappingRequest.getPlanId() + " and featureId: " + planFeaturesMappingRequest.getFeatureId());
                }
            }
        }

        if (planFeaturesMappingRequest.getIsEnabled() != null) {
            existingMapping.setIsEnabled(planFeaturesMappingRequest.getIsEnabled());
        }

        existingMapping.setUpdatedAt(LocalDateTime.now());

        PlanFeaturesMapping updatedMapping = planFeaturesMappingRepository.save(existingMapping);
        log.info("Plan features mapping updated successfully with ID: {}", updatedMapping.getId());

        return convertToResponse(updatedMapping);
    }

    public void deletePlanFeaturesMapping(Long id) {
        log.info("Deleting plan features mapping with ID: {}", id);

        if (!planFeaturesMappingRepository.existsById(id)) {
            throw new RuntimeException("Plan features mapping not found with ID: " + id);
        }

        planFeaturesMappingRepository.deleteById(id);
        log.info("Plan features mapping deleted successfully with ID: {}", id);
    }

    private PlanFeaturesMappingResponse convertToResponse(PlanFeaturesMapping mapping) {
        PlanFeaturesMappingResponse response = new PlanFeaturesMappingResponse();
        response.setId(mapping.getId());
        response.setPlanId(mapping.getPlan() != null ? mapping.getPlan().getId() : null);
        response.setFeatureId(mapping.getFeatureId());
        response.setIsEnabled(mapping.getIsEnabled());
        response.setCreatedBy(mapping.getCreatedBy() != null ? mapping.getCreatedBy().getId() : null);
        response.setUpdatedBy(mapping.getUpdatedBy() != null ? mapping.getUpdatedBy().getId() : null);
        response.setCreatedAt(mapping.getCreatedAt());
        response.setUpdatedAt(mapping.getUpdatedAt());
        return response;
    }
}
