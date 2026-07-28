package com.cafex.pos.service;

import com.cafex.pos.dto.FeatureCategoryPageResponse;
import com.cafex.pos.dto.FeatureCategoryRequest;
import com.cafex.pos.dto.FeatureCategoryResponse;
import java.util.List;
import java.util.Optional;

public interface FeatureCategoryService {
    FeatureCategoryPageResponse getFeatureCategoriesWithFilters(String name, Boolean isActive, int page, int size);
    List<FeatureCategoryResponse> getAllFeatureCategories();
    Optional<FeatureCategoryResponse> getFeatureCategoryById(Long id);
    FeatureCategoryResponse createFeatureCategory(FeatureCategoryRequest request);
    FeatureCategoryResponse updateFeatureCategory(Long id, FeatureCategoryRequest request);
    void deleteFeatureCategory(Long id);
}
