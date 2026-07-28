package com.cafex.pos.service;

import com.cafex.pos.dto.SetupFeePageResponse;
import com.cafex.pos.dto.SetupFeeRequest;
import com.cafex.pos.dto.SetupFeeResponse;
import com.cafex.pos.entity.SetupFeesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.SetupFeesMasterRepository;
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
public class SetupFeeServiceImpl implements SetupFeeService {

    private final SetupFeesMasterRepository setupFeesMasterRepository;

    @Override
    public SetupFeePageResponse getSetupFeesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching setup fees with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);

        SetupFeePageResponse allResponse = new SetupFeePageResponse();

        Specification<SetupFeesMaster> spec = (root, query, criteriaBuilder) -> {
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
            List<SetupFeesMaster> filteredFees = setupFeesMasterRepository.findAll(spec);
            List<SetupFeeResponse> content = filteredFees.stream().map(this::convertToResponse).collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<SetupFeesMaster> typePage = setupFeesMasterRepository.findAll(spec, pageable);
        List<SetupFeeResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new SetupFeePageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SetupFeeResponse> getAllSetupFees() {
        log.info("Fetching all setup fees");
        return setupFeesMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SetupFeeResponse> getSetupFeeById(Long id) {
        log.info("Fetching setup fee by ID: {}", id);
        return setupFeesMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public SetupFeeResponse createSetupFee(SetupFeeRequest request) {
        log.info("Creating new setup fee: {}", request.getName());
        if (setupFeesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Setup fee key already exists: " + request.getKey());
        }
        SetupFeesMaster type = new SetupFeesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        SetupFeesMaster saved = setupFeesMasterRepository.save(type);
        log.info("Setup fee created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public SetupFeeResponse updateSetupFee(Long id, SetupFeeRequest request) {
        log.info("Updating setup fee with ID: {}", id);
        SetupFeesMaster existing = setupFeesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setup fee not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && setupFeesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Setup fee key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        SetupFeesMaster updated = setupFeesMasterRepository.save(existing);
        log.info("Setup fee updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteSetupFee(Long id) {
        log.info("Deleting setup fee with ID: {}", id);
        if (!setupFeesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Setup fee not found with ID: " + id);
        }
        setupFeesMasterRepository.deleteById(id);
        log.info("Setup fee deleted successfully with ID: {}", id);
    }

    private SetupFeeResponse convertToResponse(SetupFeesMaster type) {
        SetupFeeResponse response = new SetupFeeResponse();
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
