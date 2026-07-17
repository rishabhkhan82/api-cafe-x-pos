package com.cafex.pos.service;

import com.cafex.pos.dto.StatePageResponse;
import com.cafex.pos.dto.StateRequest;
import com.cafex.pos.dto.StateResponse;
import com.cafex.pos.entity.StatesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.StatesMasterRepository;
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
public class StateServiceImpl implements StateService {

    private final StatesMasterRepository statesMasterRepository;

    @Override
    public StatePageResponse getStatesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching states with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        List<StateResponse> all = getAllStates();
        StatePageResponse allResponse = new StatePageResponse();
        allResponse.setData(all);
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(all.size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Specification<StatesMaster> spec = (root, query, criteriaBuilder) -> {
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

        Page<StatesMaster> typePage = statesMasterRepository.findAll(spec, pageable);
        List<StateResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new StatePageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StateResponse> getAllStates() {
        log.info("Fetching all states");
        return statesMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StateResponse> getStateById(Long id) {
        log.info("Fetching state by ID: {}", id);
        return statesMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public StateResponse createState(StateRequest request) {
        log.info("Creating new state: {}", request.getName());
        if (statesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("State key already exists: " + request.getKey());
        }
        StatesMaster type = new StatesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        StatesMaster saved = statesMasterRepository.save(type);
        log.info("State created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public StateResponse updateState(Long id, StateRequest request) {
        log.info("Updating state with ID: {}", id);
        StatesMaster existing = statesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && statesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("State key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        StatesMaster updated = statesMasterRepository.save(existing);
        log.info("State updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteState(Long id) {
        log.info("Deleting state with ID: {}", id);
        if (!statesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("State not found with ID: " + id);
        }
        statesMasterRepository.deleteById(id);
        log.info("State deleted successfully with ID: {}", id);
    }

    private StateResponse convertToResponse(StatesMaster type) {
        StateResponse response = new StateResponse();
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
