package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserRolesRequest {

    @NotBlank(message = "Role ID is required")
    @Size(max = 50, message = "Role ID must not exceed 50 characters")
    @JsonProperty("role_id")
    private String roleId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @JsonProperty("code")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @JsonProperty("description")
    private String description;

    @JsonProperty("is_system_role")
    private Boolean isSystemRole;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}