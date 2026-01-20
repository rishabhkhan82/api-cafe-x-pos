package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Data
@Slf4j
public class MenuAccessPermissionsRequest {

    @NotBlank(message = "Permission ID is required")
    @JsonProperty("permission_id")
    private String permissionId;

    @NotNull(message = "Menu ID is required")
    @JsonProperty("menu_id")
    private Long menuId;

    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    private Long roleId;

    @NotNull(message = "Can view is required")
    @JsonProperty("can_view")
    private Boolean canView;

    @NotNull(message = "Can edit is required")
    @JsonProperty("can_edit")
    private Boolean canEdit;

    @NotNull(message = "Can delete is required")
    @JsonProperty("can_delete")
    private Boolean canDelete;

    @NotNull(message = "Can create is required")
    @JsonProperty("can_create")
    private Boolean canCreate;

    // For updates - optional fields
    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_by")
    private Long createdBy;
}