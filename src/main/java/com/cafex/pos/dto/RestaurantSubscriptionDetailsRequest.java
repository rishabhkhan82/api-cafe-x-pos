package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RestaurantSubscriptionDetailsRequest {

    @JsonProperty("subscription_plan")
    private String subscriptionPlan;

    @JsonProperty("subscription_start_date")
    private LocalDateTime subscriptionStartDate;

    @JsonProperty("subscription_end_date")
    private LocalDateTime subscriptionEndDate;
}
