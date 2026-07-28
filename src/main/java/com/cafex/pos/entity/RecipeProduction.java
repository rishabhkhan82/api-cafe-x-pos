package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_productions")
public class RecipeProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "menu_item_id", nullable = false)
    private Long menuItemId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "batch_multiplier", nullable = false, precision = 38, scale = 2)
    private BigDecimal batchMultiplier;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RecipeProduction() {}

    public RecipeProduction(Long recipeId, Long menuItemId, Long restaurantId, BigDecimal batchMultiplier, String note, Long createdBy) {
        this.recipeId = recipeId;
        this.menuItemId = menuItemId;
        this.restaurantId = restaurantId;
        this.batchMultiplier = batchMultiplier;
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

    public Long getRecipeId() { return recipeId; }

    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public Long getMenuItemId() { return menuItemId; }

    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public Long getRestaurantId() { return restaurantId; }

    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public BigDecimal getBatchMultiplier() { return batchMultiplier; }

    public void setBatchMultiplier(BigDecimal batchMultiplier) { this.batchMultiplier = batchMultiplier; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public Long getCreatedBy() { return createdBy; }

    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
