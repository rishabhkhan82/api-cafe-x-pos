package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryItemResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category")
    private String category;

    @JsonProperty("unit_of_measure")
    private String unitOfMeasure;

    @JsonProperty("current_stock")
    private BigDecimal currentStock;

    @JsonProperty("minimum_stock")
    private BigDecimal minimumStock;

    @JsonProperty("maximum_stock")
    private BigDecimal maximumStock;

    @JsonProperty("unit_cost")
    private BigDecimal unitCost;

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

    @JsonProperty("last_stock_update")
    private LocalDateTime lastStockUpdate;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_by")
    private Long updatedBy;
}
