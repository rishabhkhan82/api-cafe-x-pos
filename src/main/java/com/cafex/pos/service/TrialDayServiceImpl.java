package com.cafex.pos.service;

import com.cafex.pos.dto.TrialDayPageResponse;
import com.cafex.pos.dto.TrialDayRequest;
import com.cafex.pos.dto.TrialDayResponse;
import com.cafex.pos.entity.TrialDaysMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.TrialDaysMasterRepository;
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
public class TrialDayServiceImpl implements TrialDayService {

    private final TrialDaysMasterRepository trialDaysMasterRepository;

    @Override
    public TrialDayPageResponse getTrialDaysWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching trial days with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);

        TrialDayPageResponse allResponse = new TrialDayPageResponse();

        Specification<TrialDaysMaster> spec = (root, query, criteriaBuilder) -> {
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
            List<TrialDaysMaster> filteredDays = trialDaysMasterRepository.findAll(spec);
            List<TrialDayResponse> content = filteredDays.stream().map(this::convertToResponse).collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<TrialDaysMaster> typePage = trialDaysMasterRepository.findAll(spec, pageable);
        List<TrialDayResponse> content = typePage.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());

        return new TrialDayPageResponse(content, typePage.getNumber() + 1, typePage.getTotalPages(), typePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrialDayResponse> getAllTrialDays() {
        log.info("Fetching all trial days");
        return trialDaysMasterRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrialDayResponse> getTrialDayById(Long id) {
        log.info("Fetching trial day by ID: {}", id);
        return trialDaysMasterRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public TrialDayResponse createTrialDay(TrialDayRequest request) {
        log.info("Creating new trial day: {}", request.getName());
        if (trialDaysMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Trial day key already exists: " + request.getKey());
        }
        TrialDaysMaster type = new TrialDaysMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());
        TrialDaysMaster saved = trialDaysMasterRepository.save(type);
        log.info("Trial day created successfully with ID: {}", saved.getId());
        return convertToResponse(saved);
    }

    @Override
    public TrialDayResponse updateTrialDay(Long id, TrialDayRequest request) {
        log.info("Updating trial day with ID: {}", id);
        TrialDaysMaster existing = trialDaysMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trial day not found with ID: " + id));
        if (!existing.getKey().equals(request.getKey()) && trialDaysMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Trial day key already exists: " + request.getKey());
        }
        existing.setName(request.getName());
        existing.setKey(request.getKey());
        existing.setDescription(request.getDescription());
        existing.setIsActive(request.getIsActive());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setUpdatedBy(request.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        TrialDaysMaster updated = trialDaysMasterRepository.save(existing);
        log.info("Trial day updated successfully with ID: {}", updated.getId());
        return convertToResponse(updated);
    }

    @Override
    public void deleteTrialDay(Long id) {
        log.info("Deleting trial day with ID: {}", id);
        if (!trialDaysMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trial day not found with ID: " + id);
        }
        trialDaysMasterRepository.deleteById(id);
        log.info("Trial day deleted successfully with ID: {}", id);
    }

    private TrialDayResponse convertToResponse(TrialDaysMaster type) {
        TrialDayResponse response = new TrialDayResponse();
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
