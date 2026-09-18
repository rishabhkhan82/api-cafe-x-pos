package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemAddonResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("order_item_id")
    private Long orderItemId;

    @JsonProperty("addon_id")
    private Long addonId;

    @JsonProperty("addon_name")
    private String addonName;

    @JsonProperty("addon_price")
    private BigDecimal addonPrice;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("is_required")
    private Boolean isRequired;

    @JsonProperty("min_quantity")
    private Integer minQuantity;

    @JsonProperty("max_quantity")
    private Integer maxQuantity;
}
