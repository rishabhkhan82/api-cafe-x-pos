package com.cafex.pos.controller;

import com.cafex.pos.dto.TrialDayPageResponse;
import com.cafex.pos.dto.TrialDayRequest;
import com.cafex.pos.dto.TrialDayResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.TrialDayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trial-days")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TrialDayController {

    private final TrialDayService trialDayService;

    @PostMapping
    public ResponseEntity<OperationResponse> createTrialDay(@Valid @RequestBody TrialDayRequest request) {
        log.info("Create trial day request received for key: {}", request.getKey());
        TrialDayResponse response = trialDayService.createTrialDay(request);
        log.info("Trial day created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "TRIAL_DAY_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateTrialDay(@PathVariable Long id, @Valid @RequestBody TrialDayRequest request) {
        log.info("Update trial day request received for ID: {}", id);
        TrialDayResponse response = trialDayService.updateTrialDay(id, request);
        log.info("Trial day updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "TRIAL_DAY_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<TrialDayPageResponse> getTrialDays(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get trial days request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        TrialDayPageResponse response = trialDayService.getTrialDaysWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} trial days", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrialDayResponse> getTrialDayById(@PathVariable Long id) {
        log.info("Get trial day by ID request received for ID: {}", id);
        TrialDayResponse response = trialDayService.getTrialDayById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trial day not found"));
        log.info("Trial day retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteTrialDay(@PathVariable Long id) {
        log.info("Delete trial day request received for ID: {}", id);
        trialDayService.deleteTrialDay(id);
        log.info("Trial day deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "TRIAL_DAY_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
