package com.cafex.pos.service;

import com.cafex.pos.dto.UserRolesRequest;
import com.cafex.pos.dto.UserRolesResponse;
import com.cafex.pos.dto.UserRolesPageResponse;
import com.cafex.pos.entity.UserRoles;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserRolesService {

    private final UserRolesRepository userRolesRepository;

    public UserRolesPageResponse getUserRolesWithFilters(String name, String code, Boolean isActive, int page, int size) {
        log.info("Fetching user roles with filters - name: {}, code: {}, isActive: {}, page: {}, size: {}",
                name, code, isActive, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<UserRoles> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
            }

            // Code filter
            if (code != null && !code.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("code"), code));
            }

            // IsActive filter
            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        // Use a custom query to avoid fetching permissions
        // Use a custom query to avoid fetching permissions
        Page<UserRoles> rolePage = userRolesRepository.findAll(spec, pageable);

        List<UserRolesResponse> content = rolePage.getContent().stream()
                .map(this::convertToResponseSafe)
                .collect(Collectors.toList());

        return new UserRolesPageResponse(
            content,
            rolePage.getNumber() + 1, // currentPage (1-based)
            rolePage.getTotalPages(),
            rolePage.getTotalElements()
        );
    }

    public Optional<UserRolesResponse> getUserRoleById(Long id) {
        log.info("Fetching user role by ID: {}", id);
        return userRolesRepository.findById(id)
                .map(this::convertToResponse);
    }

    public UserRolesResponse saveUserRole(UserRolesRequest userRolesRequest) {
        log.info("Saving new user role: {}", userRolesRequest.getName());

        // Check if roleId already exists
        if (userRolesRepository.existsByRoleId(userRolesRequest.getRoleId())) {
            throw new RuntimeException("Role ID already exists: " + userRolesRequest.getRoleId());
        }

        UserRoles userRole = new UserRoles();
        userRole.setRoleId(userRolesRequest.getRoleId());
        userRole.setName(userRolesRequest.getName());
        userRole.setCode(userRolesRequest.getCode());
        userRole.setDescription(userRolesRequest.getDescription());
        userRole.setIsSystemRole(userRolesRequest.getIsSystemRole());
        userRole.setIsActive(userRolesRequest.getIsActive() != null ? userRolesRequest.getIsActive() : true);
        userRole.setCreatedBy(userRolesRequest.getCreatedBy());
        userRole.setUpdatedBy(userRolesRequest.getUpdatedBy());
        userRole.setCreatedAt(userRolesRequest.getCreatedAt() != null ? userRolesRequest.getCreatedAt() : LocalDateTime.now());
        userRole.setUpdatedAt(userRolesRequest.getUpdatedAt() != null ? userRolesRequest.getUpdatedAt() : LocalDateTime.now());

        UserRoles savedRole = userRolesRepository.save(userRole);
        log.info("User role saved successfully with ID: {}", savedRole.getId());

        return convertToResponse(savedRole);
    }

    public UserRolesResponse updateUserRole(Long id, UserRolesRequest userRolesRequest) {
        log.info("Updating user role with ID: {}", id);

        UserRoles existingRole = userRolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User role not found with ID: " + id));

        // Check roleId uniqueness if changed
        if (!existingRole.getRoleId().equals(userRolesRequest.getRoleId()) &&
            userRolesRepository.existsByRoleId(userRolesRequest.getRoleId())) {
            throw new RuntimeException("Role ID already exists: " + userRolesRequest.getRoleId());
        }

        // Update fields
        existingRole.setRoleId(userRolesRequest.getRoleId());
        existingRole.setName(userRolesRequest.getName());
        existingRole.setCode(userRolesRequest.getCode());
        existingRole.setDescription(userRolesRequest.getDescription());
        existingRole.setIsSystemRole(userRolesRequest.getIsSystemRole());
        existingRole.setIsActive(userRolesRequest.getIsActive());
        existingRole.setUpdatedBy(userRolesRequest.getUpdatedBy());
        existingRole.setUpdatedAt(LocalDateTime.now());

        UserRoles updatedRole = userRolesRepository.save(existingRole);
        log.info("User role updated successfully with ID: {}", updatedRole.getId());

        return convertToResponse(updatedRole);
    }

    public void deleteUserRole(Long id) {
        log.info("Deleting user role with ID: {}", id);

        if (!userRolesRepository.existsById(id)) {
            throw new RuntimeException("User role not found with ID: " + id);
        }

        userRolesRepository.deleteById(id);
        log.info("User role deleted successfully with ID: {}", id);
    }

    private UserRolesResponse convertToResponse(UserRoles userRole) {
        UserRolesResponse response = new UserRolesResponse();
        response.setId(userRole.getId());
        response.setRoleId(userRole.getRoleId());
        response.setName(userRole.getName());
        response.setCode(userRole.getCode());
        response.setDescription(userRole.getDescription());
        response.setIsSystemRole(userRole.getIsSystemRole());
        response.setIsActive(userRole.getIsActive());
        response.setCreatedBy(userRole.getCreatedBy());
        response.setUpdatedBy(userRole.getUpdatedBy());
        response.setCreatedAt(userRole.getCreatedAt());
        response.setUpdatedAt(userRole.getUpdatedAt());
        return response;
    }

    private UserRolesResponse convertToResponseSafe(UserRoles userRole) {
        UserRolesResponse response = new UserRolesResponse();
        response.setId(userRole.getId());
        response.setRoleId(userRole.getRoleId());
        response.setName(userRole.getName());
        response.setCode(userRole.getCode());
        response.setDescription(userRole.getDescription());
        response.setIsSystemRole(userRole.getIsSystemRole());
        response.setIsActive(userRole.getIsActive());
        response.setCreatedBy(userRole.getCreatedBy());
        response.setUpdatedBy(userRole.getUpdatedBy());
        response.setCreatedAt(userRole.getCreatedAt());
        response.setUpdatedAt(userRole.getUpdatedAt());
        return response;
    }
}