package com.cafex.pos.controller;

import com.cafex.pos.dto.RoleFeaturesMappingRequest;
import com.cafex.pos.dto.RoleFeaturesMappingResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.dto.RoleFeaturesMappingPageResponse;
import com.cafex.pos.service.RoleFeaturesMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role-features-mapping")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class RoleFeaturesMappingController {

    private final RoleFeaturesMappingService roleFeaturesMappingService;

    @PostMapping
    public ResponseEntity<OperationResponse> saveRoleFeaturesMapping(@Valid @RequestBody RoleFeaturesMappingRequest roleFeaturesMappingRequest) {
        log.info("Save role features mapping request received for planId: {} and roleId: {}", roleFeaturesMappingRequest.getPlanId(), roleFeaturesMappingRequest.getRoleId());
        try {
            RoleFeaturesMappingResponse response = roleFeaturesMappingService.saveRoleFeaturesMapping(roleFeaturesMappingRequest);
            log.info("Role features mapping saved successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "ROLE_FEATURES_MAPPING_CREATED", response.getId(), null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to save role features mapping: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "ROLE_FEATURES_MAPPING_SAVE_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateRoleFeaturesMapping(@PathVariable Long id, @Valid @RequestBody RoleFeaturesMappingRequest roleFeaturesMappingRequest) {
        log.info("Update role features mapping request received for ID: {}", id);
        try {
            RoleFeaturesMappingResponse response = roleFeaturesMappingService.updateRoleFeaturesMapping(id, roleFeaturesMappingRequest);
            log.info("Role features mapping updated successfully with ID: {}", response.getId());
            OperationResponse operationResponse = new OperationResponse("success", "ROLE_FEATURES_MAPPING_UPDATED", response.getId(), response);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to update role features mapping: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "ROLE_FEATURES_MAPPING_UPDATE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }

    @GetMapping
    public ResponseEntity<RoleFeaturesMappingPageResponse> getRoleFeaturesMappings(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String featureId,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get role features mapping request received with filters - planId: {}, roleId: {}, featureId: {}, isEnabled: {}, page: {}, size: {}",
                planId, roleId, featureId, isEnabled, page, size);
        try {
            // If no filters and no pagination parameters, return all records
            if ((planId == null) &&
                (roleId == null) &&
                (featureId == null || featureId.isEmpty()) &&
                isEnabled == null &&
                page == null &&
                size == null) {
                List<RoleFeaturesMappingResponse> allMappings = roleFeaturesMappingService.getAllRoleFeaturesMappings();
                log.info("Retrieved all {} role features mappings", allMappings.size());
                RoleFeaturesMappingPageResponse response = new RoleFeaturesMappingPageResponse(
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
            RoleFeaturesMappingPageResponse response = roleFeaturesMappingService.getRoleFeaturesMappingsWithFilters(planId, roleId, featureId, isEnabled, pageValue, sizeValue);
            log.info("Retrieved {} role features mappings (page {} of {})", response.getData().size(), response.getCurrentPage(), response.getPageCount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get role features mappings: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleFeaturesMappingResponse> getRoleFeaturesMappingById(@PathVariable Long id) {
        log.info("Get role features mapping by ID request received for ID: {}", id);
        try {
            RoleFeaturesMappingResponse response = roleFeaturesMappingService.getRoleFeaturesMappingById(id)
                    .orElseThrow(() -> new RuntimeException("Role features mapping not found"));
            log.info("Role features mapping retrieved successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get role features mapping: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteRoleFeaturesMapping(@PathVariable Long id) {
        log.info("Delete role features mapping request received for ID: {}", id);
        try {
            roleFeaturesMappingService.deleteRoleFeaturesMapping(id);
            log.info("Role features mapping deleted successfully with ID: {}", id);
            OperationResponse operationResponse = new OperationResponse("success", "ROLE_FEATURES_MAPPING_DELETED", id, null);
            return ResponseEntity.ok(operationResponse);
        } catch (Exception e) {
            log.error("Failed to delete role features mapping: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "ROLE_FEATURES_MAPPING_DELETE_FAILED", id, null);
            return ResponseEntity.badRequest().body(operationResponse);
        }
    }
}
