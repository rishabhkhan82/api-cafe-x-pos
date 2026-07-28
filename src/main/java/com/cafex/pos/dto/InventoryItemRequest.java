package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryItemRequest {

    @NotBlank(message = "Item ID is required")
    @JsonProperty("item_id")
    private String itemId;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @JsonProperty("name")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Category is required")
    @Size(max = 255, message = "Category must not exceed 255 characters")
    @JsonProperty("category")
    private String category;

    @NotBlank(message = "Unit of measure is required")
    @JsonProperty("unit_of_measure")
    private String unitOfMeasure;

    @NotNull(message = "Current stock is required")
    @DecimalMin(value = "0.0", message = "Current stock must be 0 or greater")
    @JsonProperty("current_stock")
    private BigDecimal currentStock;

    @NotNull(message = "Minimum stock is required")
    @DecimalMin(value = "0.0", message = "Minimum stock must be 0 or greater")
    @JsonProperty("minimum_stock")
    private BigDecimal minimumStock;

    @DecimalMin(value = "0.0", message = "Maximum stock must be 0 or greater")
    @JsonProperty("maximum_stock")
    private BigDecimal maximumStock;

    @DecimalMin(value = "0.0", message = "Unit cost must be 0 or greater")
    @JsonProperty("unit_cost")
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0", message = "Selling price must be 0 or greater")
    @JsonProperty("selling_price")
    private BigDecimal sellingPrice;

    @JsonProperty("supplier_id")
    private String supplierId;

    @JsonProperty("location_in_store")
    private String locationInStore;

    @JsonProperty("type")
    private String type;

    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}
