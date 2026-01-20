package com.cafex.pos.dto;

import com.cafex.pos.entity.PlanFeatures.FeatureType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlanFeaturesResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("feature_id")
    private String featureId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category")
    private String category;

    @JsonProperty("category_icon")
    private String categoryIcon;

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

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}