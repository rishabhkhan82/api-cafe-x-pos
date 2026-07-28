package com.cafex.pos.service;

import com.cafex.pos.dto.FeatureTypePageResponse;
import com.cafex.pos.dto.FeatureTypeRequest;
import com.cafex.pos.dto.FeatureTypeResponse;
import java.util.List;
import java.util.Optional;

public interface FeatureTypeService {
    FeatureTypePageResponse getFeatureTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<FeatureTypeResponse> getAllFeatureTypes();
    Optional<FeatureTypeResponse> getFeatureTypeById(Long id);
    FeatureTypeResponse createFeatureType(FeatureTypeRequest request);
    FeatureTypeResponse updateFeatureType(Long id, FeatureTypeRequest request);
    void deleteFeatureType(Long id);
}
