package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemAddonRequest {

    @NotNull(message = "Addon ID is required")
    @JsonProperty("addon_id")
    private Long addonId;

    @NotBlank(message = "Addon name is required")
    @Size(max = 255, message = "Addon name must not exceed 255 characters")
    @JsonProperty("addon_name")
    private String addonName;

    @NotNull(message = "Addon price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Addon price must be non-negative")
    @JsonProperty("addon_price")
    private BigDecimal addonPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("is_required")
    private Boolean isRequired;

    @JsonProperty("min_quantity")
    private Integer minQuantity;

    @JsonProperty("max_quantity")
    private Integer maxQuantity;
}
