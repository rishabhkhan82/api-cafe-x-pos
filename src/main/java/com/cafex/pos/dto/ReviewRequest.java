package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewRequest {

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("review_text")
    private String reviewText;

    @JsonProperty("parent_review_id")
    private Long parentReviewId;

    @JsonProperty("user_type")
    private String userType;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_visible")
    private Boolean isVisible;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}
