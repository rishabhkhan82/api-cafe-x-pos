package com.cafex.pos.dto;

import com.cafex.pos.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("role")
    private User.UserRole role;

    @JsonProperty("user_type")
    private User.UserType userType;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("member_since")
    private LocalDateTime memberSince;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("is_active")
    private User.ActiveStatus isActive;

    @JsonProperty("last_login")
    private LocalDateTime lastLogin;

    // Helper method to check if user is active
    public boolean isActive() {
        return isActive == User.ActiveStatus.Y;
    }
}