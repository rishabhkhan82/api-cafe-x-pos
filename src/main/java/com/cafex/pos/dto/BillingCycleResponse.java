package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BillingCycleResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("key")
    private String key;

    @JsonProperty("description")
    private String description;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("display_order")
    private Integer displayOrder;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
