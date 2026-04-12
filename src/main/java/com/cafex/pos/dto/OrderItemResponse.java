package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("menu_item_id")
    private Long menuItemId;

    @JsonProperty("menu_item_name")
    private String menuItemName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @JsonProperty("category")
    private String category;

    @JsonProperty("special_instructions")
    private String specialInstructions;

    @JsonProperty("status")
    private String status;
}