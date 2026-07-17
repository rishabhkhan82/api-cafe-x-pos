package com.cafex.pos.controller;

import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.ReportTypePageResponse;
import com.cafex.pos.dto.ReportTypeRequest;
import com.cafex.pos.dto.ReportTypeResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.ReportTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ReportTypeController {

    private final ReportTypeService reportTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createReportType(@Valid @RequestBody ReportTypeRequest request) {
        log.info("Create report type request received for key: {}", request.getKey());
        ReportTypeResponse response = reportTypeService.createReportType(request);
        log.info("Report type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "REPORT_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateReportType(@PathVariable Long id, @Valid @RequestBody ReportTypeRequest request) {
        log.info("Update report type request received for ID: {}", id);
        ReportTypeResponse response = reportTypeService.updateReportType(id, request);
        log.info("Report type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "REPORT_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<ReportTypePageResponse> getReportTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get report types request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        ReportTypePageResponse response = reportTypeService.getReportTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} report types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportTypeResponse> getReportTypeById(@PathVariable Long id) {
        log.info("Get report type by ID request received for ID: {}", id);
        ReportTypeResponse response = reportTypeService.getReportTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report type not found"));
        log.info("Report type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteReportType(@PathVariable Long id) {
        log.info("Delete report type request received for ID: {}", id);
        reportTypeService.deleteReportType(id);
        log.info("Report type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "REPORT_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
