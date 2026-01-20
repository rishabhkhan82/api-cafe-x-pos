package com.cafex.pos.dto;

import com.cafex.pos.entity.Restaurant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RestaurantResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("gst_number")
    private String gstNumber;

    @JsonProperty("license_number")
    private String licenseNumber;

    @JsonProperty("owner_name")
    private String ownerName;

    @JsonProperty("owner_phone")
    private String ownerPhone;

    @JsonProperty("owner_email")
    private String ownerEmail;

    @JsonProperty("status")
    private Restaurant.RestaurantStatus status;

    @JsonProperty("is_active")
    private Boolean isActive;

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

    @JsonProperty("lat")
    private BigDecimal lat;

    @JsonProperty("lng")
    private BigDecimal lng;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Helper method to check if restaurant is active
    public boolean isActive() {
        return isActive != null && isActive;
    }
}