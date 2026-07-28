package com.cafex.pos.dto;

import com.cafex.pos.entity.PlanFeatures.FeatureType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlanFeaturesRequest {

    @NotBlank(message = "Feature ID is required")
    @Size(max = 50, message = "Feature ID must not exceed 50 characters")
    @JsonProperty("feature_id")
    private String featureId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Category is required")
    @JsonProperty("category")
    private String category;

    @JsonProperty("category_icon")
    private String categoryIcon;

    @NotNull(message = "Feature type is required")
    @JsonProperty("feature_type")
    private FeatureType featureType;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    @JsonProperty("sort_order")
    private Integer sortOrder;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}