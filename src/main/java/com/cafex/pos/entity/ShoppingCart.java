package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_id", nullable = false, unique = true)
    private String cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "cart_items", columnDefinition = "JSON")
    private String cartItems;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @ElementCollection
    @CollectionTable(name = "cart_applied_offers", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "offer_id")
    private List<String> appliedOffers;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "delivery_address_id")
    private String deliveryAddressId;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ShoppingCart() {}

    public ShoppingCart(String cartId, Restaurant restaurant, String sessionId, LocalDateTime expiresAt) {
        this.cartId = cartId;
        this.restaurant = restaurant;
        this.sessionId = sessionId;
        this.expiresAt = expiresAt;
        this.isActive = true;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.orderType = "dine_in";
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCartItems() {
        return cartItems;
    }

    public void setCartItems(String cartItems) {
        this.cartItems = cartItems;
        this.lastModified = LocalDateTime.now();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
        this.recalculateTotal();
        this.lastModified = LocalDateTime.now();
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
        this.recalculateTotal();
        this.lastModified = LocalDateTime.now();
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        this.recalculateTotal();
        this.lastModified = LocalDateTime.now();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<String> getAppliedOffers() {
        return appliedOffers;
    }

    public void setAppliedOffers(List<String> appliedOffers) {
        this.appliedOffers = appliedOffers;
        this.lastModified = LocalDateTime.now();
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
        this.lastModified = LocalDateTime.now();
    }

    public String getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
        this.lastModified = LocalDateTime.now();
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
        this.lastModified = LocalDateTime.now();
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isEmpty() {
        return subtotal.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean hasItems() {
        return !isEmpty();
    }

    public boolean isDineIn() {
        return "dine_in".equals(orderType);
    }

    public boolean isTakeaway() {
        return "takeaway".equals(orderType);
    }

    public boolean isDelivery() {
        return "delivery".equals(orderType);
    }

    public void addItem(String itemJson) {
        // This would be handled by service layer to update cart_items JSON
        this.lastModified = LocalDateTime.now();
    }

    public void removeItem(String itemId) {
        // This would be handled by service layer to update cart_items JSON
        this.lastModified = LocalDateTime.now();
    }

    public void clearCart() {
        this.cartItems = null;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.appliedOffers = null;
        this.specialInstructions = null;
        this.lastModified = LocalDateTime.now();
    }

    public void applyOffer(String offerId) {
        if (this.appliedOffers == null) {
            this.appliedOffers = new java.util.ArrayList<>();
        }
        if (!this.appliedOffers.contains(offerId)) {
            this.appliedOffers.add(offerId);
            this.lastModified = LocalDateTime.now();
        }
    }

    public void removeOffer(String offerId) {
        if (this.appliedOffers != null) {
            this.appliedOffers.remove(offerId);
            this.lastModified = LocalDateTime.now();
        }
    }

    public void extendExpiry(int minutes) {
        this.expiresAt = LocalDateTime.now().plusMinutes(minutes);
    }

    public long getMinutesUntilExpiry() {
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }

    public boolean isExpiringSoon() {
        return getMinutesUntilExpiry() < 30; // Less than 30 minutes
    }

    private void recalculateTotal() {
        this.totalAmount = this.subtotal.add(this.taxAmount).subtract(this.discountAmount);
    }

    public void updateAmounts(BigDecimal subtotal, BigDecimal taxAmount, BigDecimal discountAmount) {
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.recalculateTotal();
        this.lastModified = LocalDateTime.now();
    }
}
