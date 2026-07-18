package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantStatusPageResponse;
import com.cafex.pos.dto.RestaurantStatusRequest;
import com.cafex.pos.dto.RestaurantStatusResponse;
import com.cafex.pos.entity.RestaurantStatusesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.RestaurantStatusesMasterRepository;
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
public class RestaurantStatusServiceImpl implements RestaurantStatusService {

    private final RestaurantStatusesMasterRepository restaurantStatusesMasterRepository;

    @Override
    public RestaurantStatusPageResponse getRestaurantStatusesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching restaurant statuses with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);

        RestaurantStatusPageResponse allResponse = new RestaurantStatusPageResponse();

        Specification<RestaurantStatusesMaster> spec = (root, query, criteriaBuilder) -> {
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
            List<RestaurantStatusesMaster> filteredStatuses = restaurantStatusesMasterRepository.findAll(spec);
            List<RestaurantStatusResponse> content = filteredStatuses.stream().map(this::convertToResponse).collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<RestaurantStatusesMaster> typePage = restaurantStatusesMasterRepository.findAll(spec, pageable);
        List<RestaurantStatusResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new RestaurantStatusPageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantStatusResponse> getAllRestaurantStatuses() {
        log.info("Fetching all restaurant statuses");
        return restaurantStatusesMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantStatusResponse> getRestaurantStatusById(Long id) {
        log.info("Fetching restaurant status by ID: {}", id);
        return restaurantStatusesMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public RestaurantStatusResponse createRestaurantStatus(RestaurantStatusRequest request) {
        log.info("Creating new restaurant status: {}", request.getName());
        if (restaurantStatusesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Restaurant status key already exists: " + request.getKey());
        }
        RestaurantStatusesMaster type = new RestaurantStatusesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        RestaurantStatusesMaster saved = restaurantStatusesMasterRepository.save(type);
        log.info("Restaurant status created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public RestaurantStatusResponse updateRestaurantStatus(Long id, RestaurantStatusRequest request) {
        log.info("Updating restaurant status with ID: {}", id);
        RestaurantStatusesMaster existing = restaurantStatusesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant status not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && restaurantStatusesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Restaurant status key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        RestaurantStatusesMaster updated = restaurantStatusesMasterRepository.save(existing);
        log.info("Restaurant status updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteRestaurantStatus(Long id) {
        log.info("Deleting restaurant status with ID: {}", id);
        if (!restaurantStatusesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant status not found with ID: " + id);
        }
        restaurantStatusesMasterRepository.deleteById(id);
        log.info("Restaurant status deleted successfully with ID: {}", id);
    }

    private RestaurantStatusResponse convertToResponse(RestaurantStatusesMaster type) {
        RestaurantStatusResponse response = new RestaurantStatusResponse();
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
