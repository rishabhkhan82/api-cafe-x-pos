package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlanFeaturesMappingResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("plan_id")
    private Long planId;

    @JsonProperty("feature_id")
    private String featureId;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}