package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PromotionalBannerRequest {

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("display_order")
    private Integer displayOrder;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}
