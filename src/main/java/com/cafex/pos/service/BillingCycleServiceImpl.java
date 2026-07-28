package com.cafex.pos.service;

import com.cafex.pos.dto.BillingCyclePageResponse;
import com.cafex.pos.dto.BillingCycleRequest;
import com.cafex.pos.dto.BillingCycleResponse;
import com.cafex.pos.entity.BillingCyclesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.BillingCyclesMasterRepository;
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
public class BillingCycleServiceImpl implements BillingCycleService {

    private final BillingCyclesMasterRepository billingCyclesMasterRepository;

    @Override
    public BillingCyclePageResponse getBillingCyclesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching billing cycles with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);

        BillingCyclePageResponse allResponse = new BillingCyclePageResponse();

        Specification<BillingCyclesMaster> spec = (root, query, criteriaBuilder) -> {
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
            List<BillingCyclesMaster> filteredCycles = billingCyclesMasterRepository.findAll(spec);
            List<BillingCycleResponse> content = filteredCycles.stream().map(this::convertToResponse).collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<BillingCyclesMaster> typePage = billingCyclesMasterRepository.findAll(spec, pageable);
        List<BillingCycleResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new BillingCyclePageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingCycleResponse> getAllBillingCycles() {
        log.info("Fetching all billing cycles");
        return billingCyclesMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BillingCycleResponse> getBillingCycleById(Long id) {
        log.info("Fetching billing cycle by ID: {}", id);
        return billingCyclesMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public BillingCycleResponse createBillingCycle(BillingCycleRequest request) {
        log.info("Creating new billing cycle: {}", request.getName());
        if (billingCyclesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Billing cycle key already exists: " + request.getKey());
        }
        BillingCyclesMaster type = new BillingCyclesMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        BillingCyclesMaster saved = billingCyclesMasterRepository.save(type);
        log.info("Billing cycle created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public BillingCycleResponse updateBillingCycle(Long id, BillingCycleRequest request) {
        log.info("Updating billing cycle with ID: {}", id);
        BillingCyclesMaster existing = billingCyclesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing cycle not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && billingCyclesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Billing cycle key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        BillingCyclesMaster updated = billingCyclesMasterRepository.save(existing);
        log.info("Billing cycle updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteBillingCycle(Long id) {
        log.info("Deleting billing cycle with ID: {}", id);
        if (!billingCyclesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Billing cycle not found with ID: " + id);
        }
        billingCyclesMasterRepository.deleteById(id);
        log.info("Billing cycle deleted successfully with ID: {}", id);
    }

    private BillingCycleResponse convertToResponse(BillingCyclesMaster type) {
        BillingCycleResponse response = new BillingCycleResponse();
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
