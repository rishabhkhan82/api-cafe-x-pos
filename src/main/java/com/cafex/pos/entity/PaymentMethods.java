package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
public class PaymentMethods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_method_id", nullable = false, unique = true)
    private String paymentMethodId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column
    private String provider;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "processing_fee", nullable = false)
    private BigDecimal processingFee = BigDecimal.ZERO;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @Column(name = "settlement_period", nullable = false)
    private Integer settlementPeriod = 1;

    @Column(columnDefinition = "TEXT")
    private String configuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public PaymentMethods() {}

    public PaymentMethods(String paymentMethodId, String name, String type, String provider) {
        this.paymentMethodId = paymentMethodId;
        this.name = name;
        this.type = type;
        this.provider = provider;
        this.isActive = true;
        this.processingFee = BigDecimal.ZERO;
        this.settlementPeriod = 1;
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

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getSettlementPeriod() {
        return settlementPeriod;
    }

    public void setSettlementPeriod(Integer settlementPeriod) {
        this.settlementPeriod = settlementPeriod;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean isCash() {
        return "cash".equals(type);
    }

    public boolean isCard() {
        return "card".equals(type);
    }

    public boolean isDigital() {
        return "digital".equals(type);
    }

    public boolean isWallet() {
        return "wallet".equals(type);
    }

    public boolean isAmountValid(BigDecimal amount) {
        if (minAmount != null && amount.compareTo(minAmount) < 0) {
            return false;
        }
        if (maxAmount != null && amount.compareTo(maxAmount) > 0) {
            return false;
        }
        return true;
    }

    public BigDecimal calculateProcessingFee(BigDecimal amount) {
        return amount.multiply(processingFee).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal calculateTotalAmount(BigDecimal baseAmount) {
        return baseAmount.add(calculateProcessingFee(baseAmount));
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasConfiguration() {
        return configuration != null && !configuration.trim().isEmpty();
    }

    public boolean isRestaurantSpecific() {
        return restaurant != null;
    }

    public boolean isSystemWide() {
        return restaurant == null;
    }
}
