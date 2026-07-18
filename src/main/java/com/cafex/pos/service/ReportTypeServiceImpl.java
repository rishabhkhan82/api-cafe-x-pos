package com.cafex.pos.service;

import com.cafex.pos.dto.ReportTypePageResponse;
import com.cafex.pos.dto.ReportTypeRequest;
import com.cafex.pos.dto.ReportTypeResponse;
import com.cafex.pos.entity.ReportTypeMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.ReportTypeMasterRepository;
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
public class ReportTypeServiceImpl implements ReportTypeService {

    private final ReportTypeMasterRepository reportTypeMasterRepository;

    @Override
    public ReportTypePageResponse getReportTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching report types with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        ReportTypePageResponse allResponse = new ReportTypePageResponse();

        Specification<ReportTypeMaster> spec = (root, query, criteriaBuilder) -> {
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
            List<ReportTypeMaster> filteredReports = reportTypeMasterRepository.findAll(spec);
            List<ReportTypeResponse> content = filteredReports.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<ReportTypeMaster> reportPage = reportTypeMasterRepository.findAll(spec, pageable);

        List<ReportTypeResponse> content = reportPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new ReportTypePageResponse(
                content,
                reportPage.getNumber() + 1,
                reportPage.getTotalPages(),
                reportPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportTypeResponse> getAllReportTypes() {
        log.info("Fetching all report types");
        List<ReportTypeMaster> reports = reportTypeMasterRepository.findAll();
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportTypeResponse> getReportTypeById(Long id) {
        log.info("Fetching report type by ID: {}", id);
        return reportTypeMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public ReportTypeResponse createReportType(ReportTypeRequest request) {
        log.info("Creating new report type: {}", request.getName());

        if (reportTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Report type key already exists: " + request.getKey());
        }

        ReportTypeMaster report = new ReportTypeMaster();
        report.setName(request.getName());
        report.setKey(request.getKey());
        report.setDescription(request.getDescription());
        report.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        report.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        report.setCreatedBy(request.getCreatedBy());
        report.setUpdatedBy(request.getUpdatedBy());

        ReportTypeMaster savedReport = reportTypeMasterRepository.save(report);
        log.info("Report type created successfully with ID: {}", savedReport.getId());

        return convertToResponse(savedReport);
    }

    @Override
    public ReportTypeResponse updateReportType(Long id, ReportTypeRequest request) {
        log.info("Updating report type with ID: {}", id);

        ReportTypeMaster existingReport = reportTypeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report type not found with ID: " + id));

        if (!existingReport.getKey().equals(request.getKey()) &&
                reportTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Report type key already exists: " + request.getKey());
        }

        existingReport.setName(request.getName());
        existingReport.setKey(request.getKey());
        existingReport.setDescription(request.getDescription());
        existingReport.setIsActive(request.getIsActive());
        existingReport.setDisplayOrder(request.getDisplayOrder());
        existingReport.setUpdatedBy(request.getUpdatedBy());
        existingReport.setUpdatedAt(LocalDateTime.now());

        ReportTypeMaster updatedReport = reportTypeMasterRepository.save(existingReport);
        log.info("Report type updated successfully with ID: {}", updatedReport.getId());

        return convertToResponse(updatedReport);
    }

    @Override
    public void deleteReportType(Long id) {
        log.info("Deleting report type with ID: {}", id);

        if (!reportTypeMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Report type not found with ID: " + id);
        }

        reportTypeMasterRepository.deleteById(id);
        log.info("Report type deleted successfully with ID: {}", id);
    }

    private ReportTypeResponse convertToResponse(ReportTypeMaster report) {
        ReportTypeResponse response = new ReportTypeResponse();
        response.setId(report.getId());
        response.setName(report.getName());
        response.setKey(report.getKey());
        response.setDescription(report.getDescription());
        response.setIsActive(report.getIsActive());
        response.setDisplayOrder(report.getDisplayOrder());
        response.setCreatedBy(report.getCreatedBy());
        response.setUpdatedBy(report.getUpdatedBy());
        response.setCreatedAt(report.getCreatedAt());
        response.setUpdatedAt(report.getUpdatedAt());
        return response;
    }
}
