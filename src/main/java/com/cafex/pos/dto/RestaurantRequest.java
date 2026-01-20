package com.cafex.pos.dto;

import com.cafex.pos.entity.Restaurant;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Slf4j
public class RestaurantRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9\\s-]{10,20}$", message = "Phone must be a valid number")
    @JsonProperty("phone")
    private String phone;

    public void setPhone(String phone) {
        log.info("Phone value received: '{}'", phone);
        this.phone = phone;
    }

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @JsonProperty("email")
    private String email;

    @JsonProperty("gst_number")
    private String gstNumber;

    @JsonProperty("license_number")
    private String licenseNumber;

    @JsonProperty("owner_name")
    private String ownerName;

    @Pattern(regexp = "^[+]?[0-9\\s-]{10,20}$", message = "Owner phone must be a valid number")
    @JsonProperty("owner_phone")
    private String ownerPhone;

    @Email(message = "Owner email must be valid")
    @JsonProperty("owner_email")
    private String ownerEmail;

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private Restaurant.RestaurantStatus status = Restaurant.RestaurantStatus.ACTIVE;

    @NotNull(message = "Active status is required")
    @JsonProperty("is_active")
    private Boolean isActive = true;

    @JsonProperty("subscription_plan")
    private String subscriptionPlan;

    @JsonProperty("subscription_start_date")
    private LocalDateTime subscriptionStartDate;

    @JsonProperty("subscription_end_date")
    private LocalDateTime subscriptionEndDate;

    @JsonProperty("logo_image")
    private String logoImage;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;

    @JsonProperty("state")
    private Integer state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("pincode")
    private Integer pincode;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @JsonProperty("lat")
    private BigDecimal lat;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @JsonProperty("lng")
    private BigDecimal lng;

    // For updates - optional fields
    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}