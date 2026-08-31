package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MenuItemAddonResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("menu_item_id")
    private Long menuItemId;

    @JsonProperty("addon_id")
    private Long addonId;

    @JsonProperty("addon_name")
    private String addonName;

    @JsonProperty("addon_price")
    private BigDecimal addonPrice;

    @JsonProperty("addon_image")
    private String addonImage;

    @JsonProperty("is_required")
    private Boolean isRequired;

    @JsonProperty("min_quantity")
    private Integer minQuantity;

    @JsonProperty("max_quantity")
    private Integer maxQuantity;

    @JsonProperty("display_order")
    private Integer displayOrder;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
