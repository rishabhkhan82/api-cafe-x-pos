package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "business_rules_configurations")
public class BusinessRulesConfigurations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "minimum_order_value")
    private BigDecimal minimumOrderValue;

    @Column(name = "free_delivery_threshold")
    private BigDecimal freeDeliveryThreshold;

    @Column(name = "loyalty_points_per_rupee")
    private BigDecimal loyaltyPointsPerRupee;

    @Column(name = "max_discount_percentage")
    private BigDecimal maxDiscountPercentage;

    @Column(name = "service_charge_percentage")
    private BigDecimal serviceChargePercentage;

    @Column(name = "gst_percentage")
    private BigDecimal gstPercentage;

    @Column(name = "reservation_advance_hours")
    private Integer reservationAdvanceHours;

    @Column(name = "table_turnover_time")
    private Integer tableTurnoverTime;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public BusinessRulesConfigurations() {}

    public BusinessRulesConfigurations(BigDecimal minimumOrderValue, BigDecimal freeDeliveryThreshold,
                                     BigDecimal loyaltyPointsPerRupee, BigDecimal maxDiscountPercentage,
                                     BigDecimal serviceChargePercentage, BigDecimal gstPercentage,
                                     Integer reservationAdvanceHours, Integer tableTurnoverTime) {
        this.minimumOrderValue = minimumOrderValue;
        this.freeDeliveryThreshold = freeDeliveryThreshold;
        this.loyaltyPointsPerRupee = loyaltyPointsPerRupee;
        this.maxDiscountPercentage = maxDiscountPercentage;
        this.serviceChargePercentage = serviceChargePercentage;
        this.gstPercentage = gstPercentage;
        this.reservationAdvanceHours = reservationAdvanceHours;
        this.tableTurnoverTime = tableTurnoverTime;
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

    public BigDecimal getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(BigDecimal minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public BigDecimal getFreeDeliveryThreshold() {
        return freeDeliveryThreshold;
    }

    public void setFreeDeliveryThreshold(BigDecimal freeDeliveryThreshold) {
        this.freeDeliveryThreshold = freeDeliveryThreshold;
    }

    public BigDecimal getLoyaltyPointsPerRupee() {
        return loyaltyPointsPerRupee;
    }

    public void setLoyaltyPointsPerRupee(BigDecimal loyaltyPointsPerRupee) {
        this.loyaltyPointsPerRupee = loyaltyPointsPerRupee;
    }

    public BigDecimal getMaxDiscountPercentage() {
        return maxDiscountPercentage;
    }

    public void setMaxDiscountPercentage(BigDecimal maxDiscountPercentage) {
        this.maxDiscountPercentage = maxDiscountPercentage;
    }

    public BigDecimal getServiceChargePercentage() {
        return serviceChargePercentage;
    }

    public void setServiceChargePercentage(BigDecimal serviceChargePercentage) {
        this.serviceChargePercentage = serviceChargePercentage;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public Integer getReservationAdvanceHours() {
        return reservationAdvanceHours;
    }

    public void setReservationAdvanceHours(Integer reservationAdvanceHours) {
        this.reservationAdvanceHours = reservationAdvanceHours;
    }

    public Integer getTableTurnoverTime() {
        return tableTurnoverTime;
    }

    public void setTableTurnoverTime(Integer tableTurnoverTime) {
        this.tableTurnoverTime = tableTurnoverTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
