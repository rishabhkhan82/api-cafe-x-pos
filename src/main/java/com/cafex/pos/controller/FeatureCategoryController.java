package com.cafex.pos.controller;

import com.cafex.pos.dto.FeatureCategoryPageResponse;
import com.cafex.pos.dto.FeatureCategoryRequest;
import com.cafex.pos.dto.FeatureCategoryResponse;
import com.cafex.pos.dto.OperationResponse;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.service.FeatureCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feature-categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class FeatureCategoryController {

    private final FeatureCategoryService featureCategoryService;

    @PostMapping
    public ResponseEntity<OperationResponse> createFeatureCategory(@Valid @RequestBody FeatureCategoryRequest request) {
        log.info("Create feature category request received for key: {}", request.getKey());
        FeatureCategoryResponse response = featureCategoryService.createFeatureCategory(request);
        log.info("Feature category created successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "FEATURE_CATEGORY_CREATED", response.getId(), null);
        return ResponseEntity.ok(operationResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationResponse> updateFeatureCategory(@PathVariable Long id, @Valid @RequestBody FeatureCategoryRequest request) {
        log.info("Update feature category request received for ID: {}", id);
        FeatureCategoryResponse response = featureCategoryService.updateFeatureCategory(id, request);
        log.info("Feature category updated successfully with ID: {}", response.getId());
        OperationResponse operationResponse = new OperationResponse("success", "FEATURE_CATEGORY_UPDATED", response.getId(), response);
        return ResponseEntity.ok(operationResponse);
    }

    @GetMapping
    public ResponseEntity<FeatureCategoryPageResponse> getFeatureCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Get feature categories request received with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 0;

        FeatureCategoryPageResponse response = featureCategoryService.getFeatureCategoriesWithFilters(name, isActive, pageValue, sizeValue);
        log.info("Retrieved {} feature categories", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureCategoryResponse> getFeatureCategoryById(@PathVariable Long id) {
        log.info("Get feature category by ID request received for ID: {}", id);
        FeatureCategoryResponse response = featureCategoryService.getFeatureCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature category not found"));
        log.info("Feature category retrieved successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationResponse> deleteFeatureCategory(@PathVariable Long id) {
        log.info("Delete feature category request received for ID: {}", id);
        featureCategoryService.deleteFeatureCategory(id);
        log.info("Feature category deleted successfully with ID: {}", id);
        OperationResponse operationResponse = new OperationResponse("success", "FEATURE_CATEGORY_DELETED", id, null);
        return ResponseEntity.ok(operationResponse);
    }
}
