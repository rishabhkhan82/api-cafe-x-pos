package com.cafex.pos.service;

import com.cafex.pos.dto.NavigationMenuTypePageResponse;
import com.cafex.pos.dto.NavigationMenuTypeRequest;
import com.cafex.pos.dto.NavigationMenuTypeResponse;
import com.cafex.pos.entity.NavigationMenuTypesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.NavigationMenuTypesMasterRepository;
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
public class NavigationMenuTypeServiceImpl implements NavigationMenuTypeService {

    private final NavigationMenuTypesMasterRepository navigationMenuTypesMasterRepository;

    @Override
    public NavigationMenuTypePageResponse getNavigationMenuTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching navigation menu types with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);

        List<NavigationMenuTypeResponse> all = getAllNavigationMenuTypes();
        NavigationMenuTypePageResponse allResponse = new NavigationMenuTypePageResponse();
        allResponse.setData(all);
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(all.size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Specification<NavigationMenuTypesMaster> spec = (root, query, criteriaBuilder) -> {
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

        Page<NavigationMenuTypesMaster> typePage = navigationMenuTypesMasterRepository.findAll(spec, pageable);
        List<NavigationMenuTypeResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new NavigationMenuTypePageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NavigationMenuTypeResponse> getAllNavigationMenuTypes() {
        log.info("Fetching all navigation menu types");
        return navigationMenuTypesMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NavigationMenuTypeResponse> getNavigationMenuTypeById(Long id) {
        log.info("Fetching navigation menu type by ID: {}", id);
        return navigationMenuTypesMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public NavigationMenuTypeResponse createNavigationMenuType(NavigationMenuTypeRequest request) {
        log.info("Creating new navigation menu type: {}", request.getName());
        if (navigationMenuTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Navigation menu type key already exists: " + request.getKey());
        }
        NavigationMenuTypesMaster type = new NavigationMenuTypesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        NavigationMenuTypesMaster saved = navigationMenuTypesMasterRepository.save(type);
        log.info("Navigation menu type created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public NavigationMenuTypeResponse updateNavigationMenuType(Long id, NavigationMenuTypeRequest request) {
        log.info("Updating navigation menu type with ID: {}", id);
        NavigationMenuTypesMaster existing = navigationMenuTypesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Navigation menu type not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && navigationMenuTypesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Navigation menu type key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        NavigationMenuTypesMaster updated = navigationMenuTypesMasterRepository.save(existing);
        log.info("Navigation menu type updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteNavigationMenuType(Long id) {
        log.info("Deleting navigation menu type with ID: {}", id);
        if (!navigationMenuTypesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Navigation menu type not found with ID: " + id);
        }
        navigationMenuTypesMasterRepository.deleteById(id);
        log.info("Navigation menu type deleted successfully with ID: {}", id);
    }

    private NavigationMenuTypeResponse convertToResponse(NavigationMenuTypesMaster type) {
        NavigationMenuTypeResponse response = new NavigationMenuTypeResponse();
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
