package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("total_orders")
    private Integer totalOrders;

    @JsonProperty("total_spent")
    private BigDecimal totalSpent;

    @JsonProperty("loyalty_points")
    private Integer loyaltyPoints;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("restaurant_name")
    private String restaurantName;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}