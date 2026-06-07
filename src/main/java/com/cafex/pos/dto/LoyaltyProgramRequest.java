package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoyaltyProgramRequest {

    @NotBlank(message = "Program ID is required")
    @Size(max = 255, message = "Program ID must not exceed 255 characters")
    @JsonProperty("program_id")
    private String programId;

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("program_name")
    @Size(max = 255, message = "Program name must not exceed 255 characters")
    private String programName;

    @NotNull(message = "Points balance is required")
    @JsonProperty("points_balance")
    private Integer pointsBalance = 0;

    @NotNull(message = "Total points earned is required")
    @JsonProperty("total_points_earned")
    private Integer totalPointsEarned = 0;

    @NotNull(message = "Total points redeemed is required")
    @JsonProperty("total_points_redeemed")
    private Integer totalPointsRedeemed = 0;

    @JsonProperty("tier")
    private String tier;

    @JsonProperty("tier_expiry_date")
    private LocalDateTime tierExpiryDate;

    @JsonProperty("last_activity_date")
    private LocalDateTime lastActivityDate;

    @NotNull(message = "Is active is required")
    @JsonProperty("is_active")
    private Boolean isActive = true;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
