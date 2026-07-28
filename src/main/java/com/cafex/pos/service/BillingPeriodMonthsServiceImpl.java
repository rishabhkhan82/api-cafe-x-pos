package com.cafex.pos.service;

import com.cafex.pos.dto.BillingPeriodMonthsPageResponse;
import com.cafex.pos.dto.BillingPeriodMonthsRequest;
import com.cafex.pos.dto.BillingPeriodMonthsResponse;
import com.cafex.pos.entity.BillingPeriodMonthsMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.BillingPeriodMonthsMasterRepository;
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
public class BillingPeriodMonthsServiceImpl implements BillingPeriodMonthsService {

    private final BillingPeriodMonthsMasterRepository billingPeriodMonthsMasterRepository;

    @Override
    public BillingPeriodMonthsPageResponse getBillingPeriodMonthsWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching billing period months with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        BillingPeriodMonthsPageResponse allResponse = new BillingPeriodMonthsPageResponse();

        Specification<BillingPeriodMonthsMaster> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
            }

            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        if (page == 0 && size == 0) {
            List<BillingPeriodMonthsMaster> filteredMonths = billingPeriodMonthsMasterRepository.findAll(spec);
            List<BillingPeriodMonthsResponse> content = filteredMonths.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<BillingPeriodMonthsMaster> monthPage = billingPeriodMonthsMasterRepository.findAll(spec, pageable);

        List<BillingPeriodMonthsResponse> content = monthPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new BillingPeriodMonthsPageResponse(
                content,
                monthPage.getNumber() + 1,
                monthPage.getTotalPages(),
                monthPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingPeriodMonthsResponse> getAllBillingPeriodMonths() {
        log.info("Fetching all billing period months");
        List<BillingPeriodMonthsMaster> months = billingPeriodMonthsMasterRepository.findAll();
        return months.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BillingPeriodMonthsResponse> getBillingPeriodMonthsById(Long id) {
        log.info("Fetching billing period months by ID: {}", id);
        return billingPeriodMonthsMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public BillingPeriodMonthsResponse createBillingPeriodMonths(BillingPeriodMonthsRequest request) {
        log.info("Creating new billing period months: {}", request.getName());

        if (billingPeriodMonthsMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Billing period months key already exists: " + request.getKey());
        }

        BillingPeriodMonthsMaster month = new BillingPeriodMonthsMaster();
        month.setName(request.getName());
        month.setKey(request.getKey());
        month.setDescription(request.getDescription());
        month.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        month.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        month.setCreatedBy(request.getCreatedBy());
        month.setUpdatedBy(request.getUpdatedBy());

        BillingPeriodMonthsMaster savedMonth = billingPeriodMonthsMasterRepository.save(month);
        log.info("Billing period months created successfully with ID: {}", savedMonth.getId());

        return convertToResponse(savedMonth);
    }

    @Override
    public BillingPeriodMonthsResponse updateBillingPeriodMonths(Long id, BillingPeriodMonthsRequest request) {
        log.info("Updating billing period months with ID: {}", id);

        BillingPeriodMonthsMaster existingMonth = billingPeriodMonthsMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing period months not found with ID: " + id));

        if (!existingMonth.getKey().equals(request.getKey()) &&
                billingPeriodMonthsMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Billing period months key already exists: " + request.getKey());
        }

        existingMonth.setName(request.getName());
        existingMonth.setKey(request.getKey());
        existingMonth.setDescription(request.getDescription());
        existingMonth.setIsActive(request.getIsActive());
        existingMonth.setDisplayOrder(request.getDisplayOrder());
        existingMonth.setUpdatedBy(request.getUpdatedBy());
        existingMonth.setUpdatedAt(LocalDateTime.now());

        BillingPeriodMonthsMaster updatedMonth = billingPeriodMonthsMasterRepository.save(existingMonth);
        log.info("Billing period months updated successfully with ID: {}", updatedMonth.getId());

        return convertToResponse(updatedMonth);
    }

    @Override
    public void deleteBillingPeriodMonths(Long id) {
        log.info("Deleting billing period months with ID: {}", id);

        if (!billingPeriodMonthsMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Billing period months not found with ID: " + id);
        }

        billingPeriodMonthsMasterRepository.deleteById(id);
        log.info("Billing period months deleted successfully with ID: {}", id);
    }

    private BillingPeriodMonthsResponse convertToResponse(BillingPeriodMonthsMaster month) {
        BillingPeriodMonthsResponse response = new BillingPeriodMonthsResponse();
        response.setId(month.getId());
        response.setName(month.getName());
        response.setKey(month.getKey());
        response.setDescription(month.getDescription());
        response.setIsActive(month.getIsActive());
        response.setDisplayOrder(month.getDisplayOrder());
        response.setCreatedBy(month.getCreatedBy());
        response.setUpdatedBy(month.getUpdatedBy());
        response.setCreatedAt(month.getCreatedAt());
        response.setUpdatedAt(month.getUpdatedAt());
        return response;
    }
}
