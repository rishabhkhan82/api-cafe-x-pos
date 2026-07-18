package com.cafex.pos.service;

import com.cafex.pos.dto.UserTypePageResponse;
import com.cafex.pos.dto.UserTypeRequest;
import com.cafex.pos.dto.UserTypeResponse;
import com.cafex.pos.entity.UserTypesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.UserTypesMasterRepository;
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
public class UserTypeServiceImpl implements UserTypeService {

    private final UserTypesMasterRepository userTypesMasterRepository;

    @Override
    public UserTypePageResponse getUserTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching user types with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);

        UserTypePageResponse allResponse = new UserTypePageResponse();

        Specification<UserTypesMaster> spec = (root, query, criteriaBuilder) -> {
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
            List<UserTypesMaster> filteredTypes = userTypesMasterRepository.findAll(spec);
            List<UserTypeResponse> content = filteredTypes.stream().map(this::convertToResponse).collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<UserTypesMaster> typePage = userTypesMasterRepository.findAll(spec, pageable);
        List<UserTypeResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new UserTypePageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTypeResponse> getAllUserTypes() {
        log.info("Fetching all user types");
        return userTypesMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserTypeResponse> getUserTypeById(Long id) {
        log.info("Fetching user type by ID: {}", id);
        return userTypesMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public UserTypeResponse createUserType(UserTypeRequest request) {
        log.info("Creating new user type: {}", request.getName());
        if (userTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("User type key already exists: " + request.getKey());
        }
        UserTypesMaster type = new UserTypesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        UserTypesMaster saved = userTypesMasterRepository.save(type);
        log.info("User type created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public UserTypeResponse updateUserType(Long id, UserTypeRequest request) {
        log.info("Updating user type with ID: {}", id);
        UserTypesMaster existing = userTypesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User type not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && userTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("User type key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        UserTypesMaster updated = userTypesMasterRepository.save(existing);
        log.info("User type updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteUserType(Long id) {
        log.info("Deleting user type with ID: {}", id);
        if (!userTypesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("User type not found with ID: " + id);
        }
        userTypesMasterRepository.deleteById(id);
        log.info("User type deleted successfully with ID: {}", id);
    }

    private UserTypeResponse convertToResponse(UserTypesMaster type) {
        UserTypeResponse response = new UserTypeResponse();
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
