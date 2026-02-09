package com.cafex.pos.service;

import com.cafex.pos.dto.RoleFeaturesMappingRequest;
import com.cafex.pos.dto.RoleFeaturesMappingResponse;
import com.cafex.pos.dto.RoleFeaturesMappingPageResponse;
import com.cafex.pos.entity.RoleFeaturesMapping;
import com.cafex.pos.entity.SubscriptionPlans;
import com.cafex.pos.entity.UserRoles;
import com.cafex.pos.repository.RoleFeaturesMappingRepository;
import com.cafex.pos.repository.SubscriptionPlansRepository;
import com.cafex.pos.repository.UserRolesRepository;
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
public class RoleFeaturesMappingService {

    private final RoleFeaturesMappingRepository roleFeaturesMappingRepository;
    private final SubscriptionPlansRepository subscriptionPlansRepository;
    private final UserRolesRepository userRolesRepository;

    public RoleFeaturesMappingPageResponse getRoleFeaturesMappingsWithFilters(Long planId, Long roleId, String featureId, Boolean isEnabled, int page, int size) {
        log.info("Fetching role features mappings with filters - planId: {}, roleId: {}, featureId: {}, isEnabled: {}, page: {}, size: {}",
                planId, roleId, featureId, isEnabled, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<RoleFeaturesMapping> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Plan ID filter
            if (planId != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("plan").get("id"), planId));
            }

            // Role ID filter
            if (roleId != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("role").get("id"), roleId));
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

        Page<RoleFeaturesMapping> mappingPage = roleFeaturesMappingRepository.findAll(spec, pageable);

        List<RoleFeaturesMappingResponse> content = mappingPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new RoleFeaturesMappingPageResponse(
            content,
            mappingPage.getNumber() + 1, // currentPage (1-based)
            mappingPage.getTotalPages(),
            mappingPage.getTotalElements()
        );
    }

    public Optional<RoleFeaturesMappingResponse> getRoleFeaturesMappingById(Long id) {
        log.info("Fetching role features mapping by ID: {}", id);
        return roleFeaturesMappingRepository.findById(id)
                .map(this::convertToResponse);
    }

    public List<RoleFeaturesMappingResponse> getAllRoleFeaturesMappings() {
        log.info("Fetching all role features mappings");
        List<RoleFeaturesMapping> mappings = roleFeaturesMappingRepository.findAll();
        return mappings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public RoleFeaturesMappingResponse saveRoleFeaturesMapping(RoleFeaturesMappingRequest roleFeaturesMappingRequest) {
        log.info("Saving new role features mapping for planId: {} and roleId: {}", roleFeaturesMappingRequest.getPlanId(), roleFeaturesMappingRequest.getRoleId());

        // Check if plan exists
        SubscriptionPlans plan = subscriptionPlansRepository.findById(roleFeaturesMappingRequest.getPlanId())
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with ID: " + roleFeaturesMappingRequest.getPlanId()));

        // Check if role exists
        UserRoles role = userRolesRepository.findById(roleFeaturesMappingRequest.getRoleId())
                .orElseThrow(() -> new RuntimeException("User role not found with ID: " + roleFeaturesMappingRequest.getRoleId()));

        // Check for duplicate plan-role-feature combination
        if (roleFeaturesMappingRepository.existsByPlanIdAndRoleIdAndFeatureId(
                roleFeaturesMappingRequest.getPlanId(), roleFeaturesMappingRequest.getRoleId(), roleFeaturesMappingRequest.getFeatureId())) {
            throw new RuntimeException("Role-feature mapping already exists for planId: " + roleFeaturesMappingRequest.getPlanId() + 
                    ", roleId: " + roleFeaturesMappingRequest.getRoleId() + " and featureId: " + roleFeaturesMappingRequest.getFeatureId());
        }

        RoleFeaturesMapping mapping = new RoleFeaturesMapping();
        mapping.setPlan(plan);
        mapping.setRole(role);
        mapping.setFeatureId(roleFeaturesMappingRequest.getFeatureId());
        mapping.setIsEnabled(roleFeaturesMappingRequest.getIsEnabled() != null ? roleFeaturesMappingRequest.getIsEnabled() : true);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());

        RoleFeaturesMapping savedMapping = roleFeaturesMappingRepository.save(mapping);
        log.info("Role features mapping saved successfully with ID: {}", savedMapping.getId());

        return convertToResponse(savedMapping);
    }

    public RoleFeaturesMappingResponse updateRoleFeaturesMapping(Long id, RoleFeaturesMappingRequest roleFeaturesMappingRequest) {
        log.info("Updating role features mapping with ID: {}", id);

        RoleFeaturesMapping existingMapping = roleFeaturesMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role features mapping not found with ID: " + id));

        // Check if plan exists
        SubscriptionPlans plan = subscriptionPlansRepository.findById(roleFeaturesMappingRequest.getPlanId())
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with ID: " + roleFeaturesMappingRequest.getPlanId()));

        // Check if role exists
        UserRoles role = userRolesRepository.findById(roleFeaturesMappingRequest.getRoleId())
                .orElseThrow(() -> new RuntimeException("User role not found with ID: " + roleFeaturesMappingRequest.getRoleId()));

        // Check for duplicate if plan, role or feature changed
        if (!existingMapping.getPlan().getId().equals(roleFeaturesMappingRequest.getPlanId()) ||
            !existingMapping.getRole().getId().equals(roleFeaturesMappingRequest.getRoleId()) ||
            !existingMapping.getFeatureId().equals(roleFeaturesMappingRequest.getFeatureId())) {
            if (roleFeaturesMappingRepository.existsByPlanIdAndRoleIdAndFeatureId(
                    roleFeaturesMappingRequest.getPlanId(), roleFeaturesMappingRequest.getRoleId(), roleFeaturesMappingRequest.getFeatureId())) {
                throw new RuntimeException("Role-feature mapping already exists for planId: " + roleFeaturesMappingRequest.getPlanId() + 
                        ", roleId: " + roleFeaturesMappingRequest.getRoleId() + " and featureId: " + roleFeaturesMappingRequest.getFeatureId());
            }
        }

        existingMapping.setPlan(plan);
        existingMapping.setRole(role);
        existingMapping.setFeatureId(roleFeaturesMappingRequest.getFeatureId());
        existingMapping.setIsEnabled(roleFeaturesMappingRequest.getIsEnabled());
        existingMapping.setUpdatedAt(LocalDateTime.now());

        RoleFeaturesMapping updatedMapping = roleFeaturesMappingRepository.save(existingMapping);
        log.info("Role features mapping updated successfully with ID: {}", updatedMapping.getId());

        return convertToResponse(updatedMapping);
    }

    public void deleteRoleFeaturesMapping(Long id) {
        log.info("Deleting role features mapping with ID: {}", id);

        if (!roleFeaturesMappingRepository.existsById(id)) {
            throw new RuntimeException("Role features mapping not found with ID: " + id);
        }

        roleFeaturesMappingRepository.deleteById(id);
        log.info("Role features mapping deleted successfully with ID: {}", id);
    }

    private RoleFeaturesMappingResponse convertToResponse(RoleFeaturesMapping mapping) {
        RoleFeaturesMappingResponse response = new RoleFeaturesMappingResponse();
        response.setId(mapping.getId());
        response.setPlanId(mapping.getPlan() != null ? mapping.getPlan().getId() : null);
        response.setRoleId(mapping.getRole() != null ? mapping.getRole().getId() : null);
        response.setFeatureId(mapping.getFeatureId());
        response.setIsEnabled(mapping.getIsEnabled());
        response.setCreatedBy(mapping.getCreatedBy() != null ? mapping.getCreatedBy().getId() : null);
        response.setUpdatedBy(mapping.getUpdatedBy() != null ? mapping.getUpdatedBy().getId() : null);
        response.setCreatedAt(mapping.getCreatedAt());
        response.setUpdatedAt(mapping.getUpdatedAt());
        return response;
    }
}
