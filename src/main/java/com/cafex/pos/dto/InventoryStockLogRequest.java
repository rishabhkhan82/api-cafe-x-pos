package com.cafex.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryStockLogRequest {

    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;

    @JsonProperty("inventory_item_name")
    private String inventoryItemName;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("quantity_change")
    private BigDecimal quantityChange;

    @JsonProperty("balance_after")
    private BigDecimal balanceAfter;

    @JsonProperty("type")
    private String type;

    @JsonProperty("reference_id")
    private Long referenceId;

    @JsonProperty("reference_type")
    private String referenceType;

    @JsonProperty("batch_id")
    private Long batchId;

    @JsonProperty("note")
    private String note;

    @JsonProperty("created_by")
    private Long createdBy;
}
