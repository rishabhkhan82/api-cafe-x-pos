package com.cafex.pos.controller;

import com.cafex.pos.dto.PlanFeaturesMappingRequest;
import com.cafex.pos.dto.PlanFeaturesMappingResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.PlanFeaturesMappingPageResponse;
import com.cafex.pos.service.PlanFeaturesMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plan-features-mapping")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PlanFeaturesMappingController {

    private final PlanFeaturesMappingService planFeaturesMappingService;

    @PostMapping
    public ResponseEntity<OperationResponse> savePlanFeaturesMapping(@Valid @RequestBody PlanFeaturesMappingRequest planFeaturesMappingRequest) {
        log.info("Save plan features mapping request received for planId: {}", planFeaturesMappingRequest.getPlanId());
        try {
            PlanFeaturesMappingResponse response = planFeaturesMappingService.savePlanFeaturesMapping(planFeaturesMappingRequest);
            log.info("Plan features mapping saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "PLAN_FEATURES_MAPPING_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save plan features mapping: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "PLAN_FEATURES_MAPPING_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updatePlanFeaturesMapping(@PathVariable Long id, @Valid @RequestBody PlanFeaturesMappingRequest planFeaturesMappingRequest) {
        log.info("Update plan features mapping request received for ID: {}", id);
        try {
            PlanFeaturesMappingResponse response = planFeaturesMappingService.updatePlanFeaturesMapping(id, planFeaturesMappingRequest);
            log.info("Plan features mapping updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "PLAN_FEATURES_MAPPING_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update plan features mapping: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "PLAN_FEATURES_MAPPING_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<PlanFeaturesMappingPageResponse> getPlanFeaturesMappings(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) String featureId,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get plan features mapping request received with filters - planId: {}, featureId: {}, isEnabled: {}, page: {}, size: {}",
                planId, featureId, isEnabled, page, size);
        try {
            // If no filters and no pagination parameters, return all records
            if ((planId == null) &&
                (featureId == null || featureId.isEmpty()) &&
                isEnabled == null &&
                page == null &&
                size == null) {
                List<PlanFeaturesMappingResponse> allMappings = planFeaturesMappingService.getAllPlanFeaturesMappings();
                log.info("Retrieved all {} plan features mappings", allMappings.size());
                PlanFeaturesMappingPageResponse response = new PlanFeaturesMappingPageResponse(
                    allMappings,
                    0,
                    0,
                    (long) allMappings.size()
                );
                return ResponseEntity.ok(response);
            }
            // Otherwise, use pagination
            int pageValue = (page != null) ? Math.max(0, page - 1) : 0;
            int sizeValue = (size != null) ? size : 10;
            PlanFeaturesMappingPageResponse response = planFeaturesMappingService.getPlanFeaturesMappingsWithFilters(planId, featureId, isEnabled, pageValue, sizeValue);
            log.info("Retrieved {} plan features mappings (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get plan features mappings: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanFeaturesMappingResponse> getPlanFeaturesMappingById(@PathVariable Long id) {
        log.info("Get plan features mapping by ID request received for ID: {}", id);
        try {
            PlanFeaturesMappingResponse response = planFeaturesMappingService.getPlanFeaturesMappingById(id)
                    .orElseThrow(() -> new RuntimeException("Plan features mapping not found"));
            log.info("Plan features mapping retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get plan features mapping: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deletePlanFeaturesMapping(@PathVariable Long id) {
        log.info("Delete plan features mapping request received for ID: {}", id);
        try {
            planFeaturesMappingService.deletePlanFeaturesMapping(id);
            log.info("Plan features mapping deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "PLAN_FEATURES_MAPPING_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete plan features mapping: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "PLAN_FEATURES_MAPPING_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}
