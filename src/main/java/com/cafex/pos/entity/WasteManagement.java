package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_management")
public class WasteManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "inventory_item_id")
    private Long inventoryItemId;

    @Column(name = "item_name", length = 255)
    private String itemName;

    @Column(name = "quantity", nullable = false, precision = 38, scale = 2)
    private BigDecimal quantity;

    @Column(name = "reason", nullable = false, length = 50)
    private String reason;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "waste_date", nullable = false)
    private LocalDateTime wasteDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "waste_type", length = 50)
    private String wasteType;

    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "waste_cost", precision = 38, scale = 2)
    private BigDecimal wasteCost;

    public WasteManagement() {}

    public WasteManagement(Long restaurantId, Long inventoryItemId, String itemName, BigDecimal quantity, String reason, String note, LocalDateTime wasteDate, Long createdBy) {
        this.restaurantId = restaurantId;
        this.inventoryItemId = inventoryItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.reason = reason;
        this.note = note;
        this.wasteDate = wasteDate;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getRestaurantId() { return restaurantId; }

    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Long getInventoryItemId() { return inventoryItemId; }

    public void setInventoryItemId(Long inventoryItemId) { this.inventoryItemId = inventoryItemId; }

    public String getItemName() { return itemName; }

    public void setItemName(String itemName) { this.itemName = itemName; }

    public BigDecimal getQuantity() { return quantity; }

    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public LocalDateTime getWasteDate() { return wasteDate; }

    public void setWasteDate(LocalDateTime wasteDate) { this.wasteDate = wasteDate; }

    public Long getCreatedBy() { return createdBy; }

    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getWasteType() { return wasteType; }

    public void setWasteType(String wasteType) { this.wasteType = wasteType; }

    public Long getRecipeId() { return recipeId; }

    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public BigDecimal getWasteCost() { return wasteCost; }

    public void setWasteCost(BigDecimal wasteCost) { this.wasteCost = wasteCost; }
}
