package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoyaltyProgramResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("program_id")
    private String programId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("program_name")
    private String programName;

    @JsonProperty("points_balance")
    private Integer pointsBalance;

    @JsonProperty("total_points_earned")
    private Integer totalPointsEarned;

    @JsonProperty("total_points_redeemed")
    private Integer totalPointsRedeemed;

    @JsonProperty("tier")
    private String tier;

    @JsonProperty("tier_expiry_date")
    private LocalDateTime tierExpiryDate;

    @JsonProperty("last_activity_date")
    private LocalDateTime lastActivityDate;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
