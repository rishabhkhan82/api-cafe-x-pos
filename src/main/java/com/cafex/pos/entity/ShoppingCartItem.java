package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shopping_cart_items")
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_item_id", nullable = false, unique = true)
    private String cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "menu_item_name", nullable = false)
    private String menuItemName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderCustomization> customizations = new ArrayList<>();

    // Constructors
    public ShoppingCartItem() {}

    public ShoppingCartItem(String cartItemId, ShoppingCart cart, MenuItem menuItem, Integer quantity) {
        this.cartItemId = cartItemId;
        this.cart = cart;
        this.menuItem = menuItem;
        this.menuItemName = menuItem.getName();
        this.quantity = quantity;
        this.unitPrice = menuItem.getPrice();
        this.addedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        if (menuItem != null) {
            this.menuItemName = menuItem.getName();
            this.unitPrice = menuItem.getPrice();
            calculateTotalPrice();
        }
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderCustomization> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<OrderCustomization> customizations) {
        this.customizations = customizations;
    }

    // Helper methods
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

            // Add customization prices
            if (customizations != null) {
                for (OrderCustomization customization : customizations) {
                    if (customization.getAdditionalPrice() != null) {
                        this.totalPrice = this.totalPrice.add(customization.getAdditionalPrice());
                    }
                }
            }
        }
    }

    public void addCustomization(OrderCustomization customization) {
        customizations.add(customization);
        // Note: ShoppingCartItem uses OrderCustomization but doesn't set orderItem reference
        // since it's not an actual order yet
        calculateTotalPrice();
        this.updatedAt = LocalDateTime.now();
    }

    public void removeCustomization(OrderCustomization customization) {
        customizations.remove(customization);
        customization.setOrderItem(null);
        calculateTotalPrice();
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getCustomizationTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (customizations != null) {
            for (OrderCustomization customization : customizations) {
                if (customization.getAdditionalPrice() != null) {
                    total = total.add(customization.getAdditionalPrice());
                }
            }
        }
        return total;
    }

    public boolean hasCustomizations() {
        return customizations != null && !customizations.isEmpty();
    }
}
