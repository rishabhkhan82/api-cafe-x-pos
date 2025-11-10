package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kitchen_performance_metrics")
public class KitchenPerformanceMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_id", nullable = false, unique = true)
    private String metricId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String shift;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    @Column(name = "completed_orders", nullable = false)
    private Integer completedOrders = 0;

    @Column(name = "delayed_orders", nullable = false)
    private Integer delayedOrders = 0;

    @Column(name = "average_prep_time")
    private BigDecimal averagePrepTime;

    @Column(name = "target_prep_time")
    private BigDecimal targetPrepTime;

    @Column(name = "prep_time_variance")
    private BigDecimal prepTimeVariance;

    @Column(name = "order_accuracy")
    private BigDecimal orderAccuracy;

    @Column(name = "food_wastage")
    private BigDecimal foodWastage;

    @Column(name = "customer_complaints", nullable = false)
    private Integer customerComplaints = 0;

    @Column(name = "staff_count", nullable = false)
    private Integer staffCount = 0;

    @Column(name = "peak_hour_orders", nullable = false)
    private Integer peakHourOrders = 0;

    @Column(name = "slow_hour_orders", nullable = false)
    private Integer slowHourOrders = 0;

    @Column(name = "equipment_downtime", nullable = false)
    private Integer equipmentDowntime = 0;

    @Column(name = "temperature_violations", nullable = false)
    private Integer temperatureViolations = 0;

    @Column(name = "quality_score")
    private BigDecimal qualityScore;

    @Column(name = "efficiency_score")
    private BigDecimal efficiencyScore;

    @Column(name = "cost_per_order")
    private BigDecimal costPerOrder;

    @Column(name = "revenue_per_order")
    private BigDecimal revenuePerOrder;

    @Column(name = "profit_margin")
    private BigDecimal profitMargin;

    @ElementCollection
    @CollectionTable(name = "kitchen_top_performing_items", joinColumns = @JoinColumn(name = "metric_id"))
    @Column(name = "item_id")
    private List<String> topPerformingItems;

    @ElementCollection
    @CollectionTable(name = "kitchen_under_performing_items", joinColumns = @JoinColumn(name = "metric_id"))
    @Column(name = "item_id")
    private List<String> underPerformingItems;

    @ElementCollection
    @CollectionTable(name = "kitchen_bottlenecks", joinColumns = @JoinColumn(name = "metric_id"))
    @Column(name = "bottleneck")
    private List<String> bottlenecks;

    @ElementCollection
    @CollectionTable(name = "kitchen_improvement_suggestions", joinColumns = @JoinColumn(name = "metric_id"))
    @Column(name = "suggestion")
    private List<String> improvementSuggestions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public KitchenPerformanceMetrics() {}

    public KitchenPerformanceMetrics(String metricId, Restaurant restaurant, LocalDateTime date, String shift) {
        this.metricId = metricId;
        this.restaurant = restaurant;
        this.date = date;
        this.shift = shift;
        this.totalOrders = 0;
        this.completedOrders = 0;
        this.delayedOrders = 0;
        this.customerComplaints = 0;
        this.staffCount = 0;
        this.peakHourOrders = 0;
        this.slowHourOrders = 0;
        this.equipmentDowntime = 0;
        this.temperatureViolations = 0;
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

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Integer completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Integer getDelayedOrders() {
        return delayedOrders;
    }

    public void setDelayedOrders(Integer delayedOrders) {
        this.delayedOrders = delayedOrders;
    }

    public BigDecimal getAveragePrepTime() {
        return averagePrepTime;
    }

    public void setAveragePrepTime(BigDecimal averagePrepTime) {
        this.averagePrepTime = averagePrepTime;
    }

    public BigDecimal getTargetPrepTime() {
        return targetPrepTime;
    }

    public void setTargetPrepTime(BigDecimal targetPrepTime) {
        this.targetPrepTime = targetPrepTime;
    }

    public BigDecimal getPrepTimeVariance() {
        return prepTimeVariance;
    }

    public void setPrepTimeVariance(BigDecimal prepTimeVariance) {
        this.prepTimeVariance = prepTimeVariance;
    }

    public BigDecimal getOrderAccuracy() {
        return orderAccuracy;
    }

    public void setOrderAccuracy(BigDecimal orderAccuracy) {
        this.orderAccuracy = orderAccuracy;
    }

    public BigDecimal getFoodWastage() {
        return foodWastage;
    }

    public void setFoodWastage(BigDecimal foodWastage) {
        this.foodWastage = foodWastage;
    }

    public Integer getCustomerComplaints() {
        return customerComplaints;
    }

    public void setCustomerComplaints(Integer customerComplaints) {
        this.customerComplaints = customerComplaints;
    }

    public Integer getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(Integer staffCount) {
        this.staffCount = staffCount;
    }

    public Integer getPeakHourOrders() {
        return peakHourOrders;
    }

    public void setPeakHourOrders(Integer peakHourOrders) {
        this.peakHourOrders = peakHourOrders;
    }

    public Integer getSlowHourOrders() {
        return slowHourOrders;
    }

    public void setSlowHourOrders(Integer slowHourOrders) {
        this.slowHourOrders = slowHourOrders;
    }

    public Integer getEquipmentDowntime() {
        return equipmentDowntime;
    }

    public void setEquipmentDowntime(Integer equipmentDowntime) {
        this.equipmentDowntime = equipmentDowntime;
    }

    public Integer getTemperatureViolations() {
        return temperatureViolations;
    }

    public void setTemperatureViolations(Integer temperatureViolations) {
        this.temperatureViolations = temperatureViolations;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
    }

    public BigDecimal getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(BigDecimal efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }

    public BigDecimal getCostPerOrder() {
        return costPerOrder;
    }

    public void setCostPerOrder(BigDecimal costPerOrder) {
        this.costPerOrder = costPerOrder;
    }

    public BigDecimal getRevenuePerOrder() {
        return revenuePerOrder;
    }

    public void setRevenuePerOrder(BigDecimal revenuePerOrder) {
        this.revenuePerOrder = revenuePerOrder;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public List<String> getTopPerformingItems() {
        return topPerformingItems;
    }

    public void setTopPerformingItems(List<String> topPerformingItems) {
        this.topPerformingItems = topPerformingItems;
    }

    public List<String> getUnderPerformingItems() {
        return underPerformingItems;
    }

    public void setUnderPerformingItems(List<String> underPerformingItems) {
        this.underPerformingItems = underPerformingItems;
    }

    public List<String> getBottlenecks() {
        return bottlenecks;
    }

    public void setBottlenecks(List<String> bottlenecks) {
        this.bottlenecks = bottlenecks;
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
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
}
