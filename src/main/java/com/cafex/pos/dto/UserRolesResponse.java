package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserRolesResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("role_id")
    private String roleId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

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