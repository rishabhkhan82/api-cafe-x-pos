package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_stock_log")
public class InventoryStockLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_item_id", nullable = false)
    private Long inventoryItemId;

    @Column(name = "inventory_item_name")
    private String inventoryItemName;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "quantity_change", nullable = false, precision = 38, scale = 2)
    private BigDecimal quantityChange;

    @Column(name = "balance_after", nullable = false, precision = 38, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public InventoryStockLog() {}

    public InventoryStockLog(Long inventoryItemId, Long restaurantId, BigDecimal quantityChange, BigDecimal balanceAfter, String type, Long referenceId, String referenceType, Long batchId, String note, Long createdBy) {
        this.inventoryItemId = inventoryItemId;
        this.restaurantId = restaurantId;
        this.quantityChange = quantityChange;
        this.balanceAfter = balanceAfter;
        this.type = type;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.batchId = batchId;
        this.note = note;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getInventoryItemId() { return inventoryItemId; }

    public void setInventoryItemId(Long inventoryItemId) { this.inventoryItemId = inventoryItemId; }

    public String getInventoryItemName() { return inventoryItemName; }

    public void setInventoryItemName(String inventoryItemName) { this.inventoryItemName = inventoryItemName; }

    public Long getRestaurantId() { return restaurantId; }

    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public BigDecimal getQuantityChange() { return quantityChange; }

    public void setQuantityChange(BigDecimal quantityChange) { this.quantityChange = quantityChange; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }

    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Long getReferenceId() { return referenceId; }

    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getReferenceType() { return referenceType; }

    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public Long getBatchId() { return batchId; }

    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public Long getCreatedBy() { return createdBy; }

    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
