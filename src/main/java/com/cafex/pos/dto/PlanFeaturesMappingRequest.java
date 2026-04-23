package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlanFeaturesMappingRequest {

    @JsonProperty("plan_id")
    private Long planId;

    @Size(max = 255, message = "Feature ID must not exceed 255 characters")
    @JsonProperty("feature_id")
    private String featureId;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}