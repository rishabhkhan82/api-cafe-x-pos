package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrialSubscriptionRequest {
    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurantId")
    private Long restaurantId;

    @NotNull(message = "Plan ID is required")
    @JsonProperty("planId")
    private Long planId;

    @NotNull(message = "User ID is required")
    @JsonProperty("userId")
    private Long userId;
}