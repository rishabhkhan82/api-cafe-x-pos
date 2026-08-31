package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Slf4j
public class MenuItemAddonRequest {

    @NotNull(message = "Add-on ID is required")
    @JsonProperty("addon_id")
    private Long addonId;

    @JsonProperty("is_required")
    private Boolean isRequired = false;

    @Min(value = 0, message = "Minimum quantity must be 0 or greater")
    @JsonProperty("min_quantity")
    private Integer minQuantity = 0;

    @Min(value = 1, message = "Maximum quantity must be at least 1")
    @JsonProperty("max_quantity")
    private Integer maxQuantity = 10;

    @JsonProperty("display_order")
    private Integer displayOrder = 0;
}
