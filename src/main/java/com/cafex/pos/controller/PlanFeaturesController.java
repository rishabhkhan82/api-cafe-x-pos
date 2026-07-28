package com.cafex.pos.controller;

import com.cafex.pos.dto.PlanFeaturesRequest;
import com.cafex.pos.dto.PlanFeaturesResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.PlanFeaturesPageResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.PlanFeaturesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plan-features")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PlanFeaturesController {

    private final PlanFeaturesService planFeaturesService;

    @PostMapping
    public ResponseEntity<OperationResponse> savePlanFeature(@Valid @RequestBody PlanFeaturesRequest planFeaturesRequest) {
        log.info("Save plan feature request received for featureId: {}", planFeaturesRequest.getFeatureId());
        PlanFeaturesResponse response = planFeaturesService.savePlanFeature(planFeaturesRequest);
        log.info("Plan feature saved successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "PLAN_FEATURE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updatePlanFeature(@PathVariable Long id, @Valid @RequestBody PlanFeaturesRequest planFeaturesRequest) {
        log.info("Update plan feature request received for ID: {}", id);
        PlanFeaturesResponse response = planFeaturesService.updatePlanFeature(id, planFeaturesRequest);
        log.info("Plan feature updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "PLAN_FEATURE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<PlanFeaturesPageResponse> getPlanFeatures(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(required = false) String featureType,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get plan features request received with filters - name: {}, category: {}, isEnabled: {}, featureType: {}, page: {}, size: {}",
                name, category, isEnabled, featureType, page, size);
        if ((name == null || name.isEmpty()) &&
            (category == null || category.isEmpty()) &&
            isEnabled == null &&
            (featureType == null || featureType.isEmpty()) &&
            page == null &&
            size == null) {
            List<PlanFeaturesResponse> allFeatures = planFeaturesService.getAllPlanFeatures();
            log.info("Retrieved all {} plan features", allFeatures.size());
            PlanFeaturesPageResponse response = new PlanFeaturesPageResponse(
                allFeatures,
                0,
                0,
                (long) allFeatures.size()
            );
            return ResponseEntity.ok(response);
        }
        int pageValue = (page != null) ? Math.max(0, page - 1) : 0;
        int sizeValue = (size != null) ? size : 10;
        PlanFeaturesPageResponse response = planFeaturesService.getPlanFeaturesWithFilters(name, category, isEnabled, featureType, pageValue, sizeValue);
        log.info("Retrieved {} plan features (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanFeaturesResponse> getPlanFeatureById(@PathVariable Long id) {
        log.info("Get plan feature by ID request received for ID: {}", id);
        PlanFeaturesResponse response = planFeaturesService.getPlanFeatureById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan feature not found"));
        log.info("Plan feature retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deletePlanFeature(@PathVariable Long id) {
        log.info("Delete plan feature request received for ID: {}", id);
        planFeaturesService.deletePlanFeature(id);
        log.info("Plan feature deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "PLAN_FEATURE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}