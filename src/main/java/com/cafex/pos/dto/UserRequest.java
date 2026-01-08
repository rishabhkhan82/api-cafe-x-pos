package com.cafex.pos.dto;

import com.cafex.pos.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Data
@Slf4j
public class UserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @JsonProperty("username")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9\\s-]{10,20}$", message = "Phone must be a valid number")
    @JsonProperty("phone")
    private String phone;

    public void setPhone(String phone) {
        log.info("Phone value received: '{}'", phone);
        this.phone = phone;
    }

    @NotNull(message = "Role is required")
    @JsonProperty("role")
    private User.UserRole role;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("restaurantId")
    private String restaurantId;

    @NotNull(message = "Active status is required")
    @JsonProperty("is_active")
    private User.ActiveStatus isActive;

    // For updates - optional fields
    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_by")
    private Long createdBy;

    // Additional fields as per table columns
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("member_since")
    private LocalDateTime memberSince;

    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
}