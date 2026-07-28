package com.cafex.pos.controller;

import com.cafex.pos.dto.FeatureTypePageResponse;
import com.cafex.pos.dto.FeatureTypeRequest;
import com.cafex.pos.dto.FeatureTypeResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.FeatureTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feature-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class FeatureTypeController {

    private final FeatureTypeService featureTypeService;

    @PostMapping
    public ResponseEntity<OperationResponse> createFeatureType(@Valid @RequestBody FeatureTypeRequest request) {
        log.info("Create feature type request received for key: {}", request.getKey());
        FeatureTypeResponse response = featureTypeService.createFeatureType(request);
        log.info("Feature type created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "FEATURE_TYPE_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateFeatureType(@PathVariable Long id, @Valid @RequestBody FeatureTypeRequest request) {
        log.info("Update feature type request received for ID: {}", id);
        FeatureTypeResponse response = featureTypeService.updateFeatureType(id, request);
        log.info("Feature type updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "FEATURE_TYPE_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<FeatureTypePageResponse> getFeatureTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get feature types request received with filters - name: {}, isActive: {}, page: {}, size: {}", name, isActive, page, size);
        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;
        FeatureTypePageResponse response = featureTypeService.getFeatureTypesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} feature types", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureTypeResponse> getFeatureTypeById(@PathVariable Long id) {
        log.info("Get feature type by ID request received for ID: {}", id);
        FeatureTypeResponse response = featureTypeService.getFeatureTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature type not found"));
        log.info("Feature type retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteFeatureType(@PathVariable Long id) {
        log.info("Delete feature type request received for ID: {}", id);
        featureTypeService.deleteFeatureType(id);
        log.info("Feature type deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "FEATURE_TYPE_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
