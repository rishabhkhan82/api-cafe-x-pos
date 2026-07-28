package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemRequest {

    @JsonProperty("order_id")
    private Long orderId;

    @NotNull(message = "Menu item ID is required")
    @JsonProperty("menu_item_id")
    private Long menuItemId;

    @NotBlank(message = "Menu item name is required")
    @Size(max = 255, message = "Menu item name must not exceed 255 characters")
    @JsonProperty("menu_item_name")
    private String menuItemName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @JsonProperty("quantity")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @JsonProperty("category")
    private String category;

    @Size(max = 500, message = "Special instructions must not exceed 500 characters")
    @JsonProperty("special_instructions")
    private String specialInstructions;

    @JsonProperty("status")
    private String status;

    // For updates
    @JsonProperty("id")
    private Long id;
}