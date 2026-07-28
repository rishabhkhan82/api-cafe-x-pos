package com.cafex.pos.service;

import com.cafex.pos.dto.ReportTypePageResponse;
import com.cafex.pos.dto.ReportTypeRequest;
import com.cafex.pos.dto.ReportTypeResponse;
import java.util.List;
import java.util.Optional;

public interface ReportTypeService {
    ReportTypePageResponse getReportTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<ReportTypeResponse> getAllReportTypes();
    Optional<ReportTypeResponse> getReportTypeById(Long id);
    ReportTypeResponse createReportType(ReportTypeRequest request);
    ReportTypeResponse updateReportType(Long id, ReportTypeRequest request);
    void deleteReportType(Long id);
}
