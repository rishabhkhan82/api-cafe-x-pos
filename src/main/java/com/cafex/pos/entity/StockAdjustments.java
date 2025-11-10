package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_adjustments")
public class StockAdjustments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adjustment_id", nullable = false, unique = true)
    private String adjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "adjustment_type", nullable = false)
    private String adjustmentType;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "previous_stock", nullable = false)
    private BigDecimal previousStock;

    @Column(name = "new_stock", nullable = false)
    private BigDecimal newStock;

    @Column
    private String reason;

    @Column
    private String reference;

    @Column
    private BigDecimal cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public StockAdjustments() {}

    public StockAdjustments(String adjustmentId, InventoryItem inventoryItem, String adjustmentType, BigDecimal quantity, BigDecimal previousStock, User performedBy, Restaurant restaurant) {
        this.adjustmentId = adjustmentId;
        this.inventoryItem = inventoryItem;
        this.adjustmentType = adjustmentType;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = previousStock.add(quantity);
        this.performedBy = performedBy;
        this.restaurant = restaurant;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(String adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        if (this.previousStock != null) {
            this.newStock = this.previousStock.add(quantity);
        }
    }

    public BigDecimal getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(BigDecimal previousStock) {
        this.previousStock = previousStock;
        if (this.quantity != null) {
            this.newStock = previousStock.add(quantity);
        }
    }

    public BigDecimal getNewStock() {
        return newStock;
    }

    public void setNewStock(BigDecimal newStock) {
        this.newStock = newStock;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isAddition() {
        return "addition".equals(adjustmentType);
    }

    public boolean isReduction() {
        return "reduction".equals(adjustmentType);
    }

    public boolean isCorrection() {
        return "correction".equals(adjustmentType);
    }

    public boolean isDamage() {
        return "damage".equals(adjustmentType);
    }

    public boolean isExpiry() {
        return "expiry".equals(adjustmentType);
    }

    public boolean isPositiveAdjustment() {
        return quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegativeAdjustment() {
        return quantity != null && quantity.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getAbsoluteQuantity() {
        return quantity != null ? quantity.abs() : BigDecimal.ZERO;
    }

    public boolean hasCost() {
        return cost != null && cost.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean isManualAdjustment() {
        return "correction".equals(adjustmentType);
    }

    public boolean isSystemAdjustment() {
        return !isManualAdjustment();
    }

    public String getAdjustmentDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(adjustmentType);
        if (reason != null && !reason.trim().isEmpty()) {
            desc.append(" - ").append(reason);
        }
        if (reference != null && !reference.trim().isEmpty()) {
            desc.append(" (").append(reference).append(")");
        }
        return desc.toString();
    }

    public void calculateCost(BigDecimal unitCost) {
        if (unitCost != null && quantity != null) {
            this.cost = unitCost.multiply(quantity.abs());
        }
    }

    public boolean affectsInventoryValue() {
        return hasCost() && (isAddition() || isReduction());
    }

    public BigDecimal getInventoryValueChange() {
        if (!affectsInventoryValue()) return BigDecimal.ZERO;
        return isAddition() ? cost : cost.negate();
    }
}
