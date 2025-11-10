package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient_id", nullable = false, unique = true)
    private String ingredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String unit;

    @Column
    private BigDecimal cost;

    @Column(name = "is_optional", nullable = false)
    private Boolean isOptional = false;

    @Column(name = "substitute_allowed", nullable = false)
    private Boolean substituteAllowed = false;

    @Column(name = "substitute_ingredient")
    private String substituteIngredient;

    @Column(name = "preparation_notes", columnDefinition = "TEXT")
    private String preparationNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public RecipeIngredient() {}

    public RecipeIngredient(String ingredientId, Recipe recipe, InventoryItem inventoryItem, String ingredientName, BigDecimal quantity, String unit) {
        this.ingredientId = ingredientId;
        this.recipe = recipe;
        this.inventoryItem = inventoryItem;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Boolean getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }

    public Boolean getSubstituteAllowed() {
        return substituteAllowed;
    }

    public void setSubstituteAllowed(Boolean substituteAllowed) {
        this.substituteAllowed = substituteAllowed;
    }

    public String getSubstituteIngredient() {
        return substituteIngredient;
    }

    public void setSubstituteIngredient(String substituteIngredient) {
        this.substituteIngredient = substituteIngredient;
    }

    public String getPreparationNotes() {
        return preparationNotes;
    }

    public void setPreparationNotes(String preparationNotes) {
        this.preparationNotes = preparationNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean hasSubstitute() {
        return substituteAllowed && substituteIngredient != null && !substituteIngredient.isEmpty();
    }

    public BigDecimal calculateCost() {
        if (cost != null) {
            return cost;
        }
        // Calculate cost based on inventory item unit cost if available
        if (inventoryItem != null && inventoryItem.getUnitCost() != null) {
            return inventoryItem.getUnitCost().multiply(quantity);
        }
        return BigDecimal.ZERO;
    }

    public boolean isAvailable() {
        if (inventoryItem == null) return true; // Not linked to inventory
        return inventoryItem.getCurrentStock().compareTo(quantity) >= 0;
    }

    public BigDecimal getAvailableQuantity() {
        if (inventoryItem == null) return BigDecimal.valueOf(Double.MAX_VALUE);
        return inventoryItem.getCurrentStock();
    }
}
