package com.cafex.pos.dto;

import com.cafex.pos.entity.NavigationMenu.MenuType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NavigationMenuRequest {

    @NotBlank(message = "Menu ID is required")
    @Size(max = 50, message = "Menu ID must not exceed 50 characters")
    @JsonProperty("menu_id")
    private String menuId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("parent_id")
    private String parentId;

    @NotBlank(message = "Role is required")
    @JsonProperty("role")
    private String role;

    @NotBlank(message = "Path is required")
    @JsonProperty("path")
    private String path;

    @JsonProperty("icon")
    private String icon;

    @NotNull(message = "Display order is required")
    @JsonProperty("display_order")
    private Integer displayOrder;

    @NotNull(message = "Type is required")
    @JsonProperty("type")
    private MenuType type;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;
}