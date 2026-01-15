package com.cafex.pos.dto;

import com.cafex.pos.entity.NavigationMenu.MenuType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NavigationMenuResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("menu_id")
    private String menuId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("role")
    private String role;

    @JsonProperty("path")
    private String path;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("display_order")
    private Integer displayOrder;

    @JsonProperty("type")
    private MenuType type;

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