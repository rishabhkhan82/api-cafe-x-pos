package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Data
@Slf4j
public class CustomerRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    private String name;

    @Email(message = "Email must be valid")
    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    // Custom validation to make phone optional but validate format if provided
    @AssertTrue(message = "Phone must be a valid number if provided")
    public boolean isPhoneValid() {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional
        }
        return Pattern.matches("^[+]?[0-9\\s-]{10,20}$", phone);
    }

    @JsonProperty("avatar")
    private String avatar;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    // For updates - optional fields
    @JsonProperty("id")
    private Long id;

    // Additional fields as per table columns
    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("total_orders")
    private Integer totalOrders;

    @JsonProperty("total_spent")
    private java.math.BigDecimal totalSpent;

    @JsonProperty("loyalty_points")
    private Integer loyaltyPoints;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}