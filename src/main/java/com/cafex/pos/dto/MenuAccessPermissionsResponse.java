package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MenuAccessPermissionsResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("permission_id")
    private String permissionId;

    @JsonProperty("menu_id")
    private Long menuId;

    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("can_view")
    private Boolean canView;

    @JsonProperty("can_edit")
    private Boolean canEdit;

    @JsonProperty("can_delete")
    private Boolean canDelete;

    @JsonProperty("can_create")
    private Boolean canCreate;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}