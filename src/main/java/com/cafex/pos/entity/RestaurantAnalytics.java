package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "restaurant_analytics")
public class RestaurantAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analytics_id", nullable = false, unique = true)
    private String analyticsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    @Column(name = "average_order_value")
    private BigDecimal averageOrderValue;

    @Column(name = "peak_hour")
    private String peakHour;

    @Column(name = "peak_day")
    private String peakDay;

    @Column(name = "top_menu_items", columnDefinition = "JSON")
    private String topMenuItems;

    @Column(name = "top_categories", columnDefinition = "JSON")
    private String topCategories;

    @Column(name = "customer_count", nullable = false)
    private Integer customerCount = 0;

    @Column(name = "repeat_customers", nullable = false)
    private Integer repeatCustomers = 0;

    @Column(name = "new_customers", nullable = false)
    private Integer newCustomers = 0;

    @Column(name = "customer_retention_rate")
    private BigDecimal customerRetentionRate;

    @Column(name = "table_turnover_rate")
    private BigDecimal tableTurnoverRate;

    @Column(name = "average_service_time")
    private BigDecimal averageServiceTime;

    @Column(name = "order_accuracy")
    private BigDecimal orderAccuracy;

    @Column(name = "customer_satisfaction")
    private BigDecimal customerSatisfaction;

    @Column(name = "online_orders", nullable = false)
    private Integer onlineOrders = 0;

    @Column(name = "dine_in_orders", nullable = false)
    private Integer dineInOrders = 0;

    @Column(name = "takeaway_orders", nullable = false)
    private Integer takeawayOrders = 0;

    @Column(name = "delivery_orders", nullable = false)
    private Integer deliveryOrders = 0;

    @Column(name = "payment_method_breakdown", columnDefinition = "JSON")
    private String paymentMethodBreakdown;

    @Column(name = "discount_usage")
    private BigDecimal discountUsage;

    @Column(name = "loyalty_points_earned", nullable = false)
    private Integer loyaltyPointsEarned = 0;

    @Column(name = "loyalty_points_redeemed", nullable = false)
    private Integer loyaltyPointsRedeemed = 0;

    @Column(name = "staff_efficiency")
    private BigDecimal staffEfficiency;

    @Column(name = "inventory_turnover")
    private BigDecimal inventoryTurnover;

    @Column(name = "food_cost_percentage")
    private BigDecimal foodCostPercentage;

    @Column(name = "labor_cost_percentage")
    private BigDecimal laborCostPercentage;

    @Column(name = "profit_margin")
    private BigDecimal profitMargin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public RestaurantAnalytics() {}

    public RestaurantAnalytics(String analyticsId, Restaurant restaurant, LocalDateTime date, String period) {
        this.analyticsId = analyticsId;
        this.restaurant = restaurant;
        this.date = date;
        this.period = period;
        this.totalOrders = 0;
        this.customerCount = 0;
        this.repeatCustomers = 0;
        this.newCustomers = 0;
        this.onlineOrders = 0;
        this.dineInOrders = 0;
        this.takeawayOrders = 0;
        this.deliveryOrders = 0;
        this.loyaltyPointsEarned = 0;
        this.loyaltyPointsRedeemed = 0;
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

    public String getAnalyticsId() {
        return analyticsId;
    }

    public void setAnalyticsId(String analyticsId) {
        this.analyticsId = analyticsId;
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public String getPeakHour() {
        return peakHour;
    }

    public void setPeakHour(String peakHour) {
        this.peakHour = peakHour;
    }

    public String getPeakDay() {
        return peakDay;
    }

    public void setPeakDay(String peakDay) {
        this.peakDay = peakDay;
    }

    public String getTopMenuItems() {
        return topMenuItems;
    }

    public void setTopMenuItems(String topMenuItems) {
        this.topMenuItems = topMenuItems;
    }

    public String getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(String topCategories) {
        this.topCategories = topCategories;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public Integer getRepeatCustomers() {
        return repeatCustomers;
    }

    public void setRepeatCustomers(Integer repeatCustomers) {
        this.repeatCustomers = repeatCustomers;
    }

    public Integer getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(Integer newCustomers) {
        this.newCustomers = newCustomers;
    }

    public BigDecimal getCustomerRetentionRate() {
        return customerRetentionRate;
    }

    public void setCustomerRetentionRate(BigDecimal customerRetentionRate) {
        this.customerRetentionRate = customerRetentionRate;
    }

    public BigDecimal getTableTurnoverRate() {
        return tableTurnoverRate;
    }

    public void setTableTurnoverRate(BigDecimal tableTurnoverRate) {
        this.tableTurnoverRate = tableTurnoverRate;
    }

    public BigDecimal getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(BigDecimal averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public BigDecimal getOrderAccuracy() {
        return orderAccuracy;
    }

    public void setOrderAccuracy(BigDecimal orderAccuracy) {
        this.orderAccuracy = orderAccuracy;
    }

    public BigDecimal getCustomerSatisfaction() {
        return customerSatisfaction;
    }

    public void setCustomerSatisfaction(BigDecimal customerSatisfaction) {
        this.customerSatisfaction = customerSatisfaction;
    }

    public Integer getOnlineOrders() {
        return onlineOrders;
    }

    public void setOnlineOrders(Integer onlineOrders) {
        this.onlineOrders = onlineOrders;
    }

    public Integer getDineInOrders() {
        return dineInOrders;
    }

    public void setDineInOrders(Integer dineInOrders) {
        this.dineInOrders = dineInOrders;
    }

    public Integer getTakeawayOrders() {
        return takeawayOrders;
    }

    public void setTakeawayOrders(Integer takeawayOrders) {
        this.takeawayOrders = takeawayOrders;
    }

    public Integer getDeliveryOrders() {
        return deliveryOrders;
    }

    public void setDeliveryOrders(Integer deliveryOrders) {
        this.deliveryOrders = deliveryOrders;
    }

    public String getPaymentMethodBreakdown() {
        return paymentMethodBreakdown;
    }

    public void setPaymentMethodBreakdown(String paymentMethodBreakdown) {
        this.paymentMethodBreakdown = paymentMethodBreakdown;
    }

    public BigDecimal getDiscountUsage() {
        return discountUsage;
    }

    public void setDiscountUsage(BigDecimal discountUsage) {
        this.discountUsage = discountUsage;
    }

    public Integer getLoyaltyPointsEarned() {
        return loyaltyPointsEarned;
    }

    public void setLoyaltyPointsEarned(Integer loyaltyPointsEarned) {
        this.loyaltyPointsEarned = loyaltyPointsEarned;
    }

    public Integer getLoyaltyPointsRedeemed() {
        return loyaltyPointsRedeemed;
    }

    public void setLoyaltyPointsRedeemed(Integer loyaltyPointsRedeemed) {
        this.loyaltyPointsRedeemed = loyaltyPointsRedeemed;
    }

    public BigDecimal getStaffEfficiency() {
        return staffEfficiency;
    }

    public void setStaffEfficiency(BigDecimal staffEfficiency) {
        this.staffEfficiency = staffEfficiency;
    }

    public BigDecimal getInventoryTurnover() {
        return inventoryTurnover;
    }

    public void setInventoryTurnover(BigDecimal inventoryTurnover) {
        this.inventoryTurnover = inventoryTurnover;
    }

    public BigDecimal getFoodCostPercentage() {
        return foodCostPercentage;
    }

    public void setFoodCostPercentage(BigDecimal foodCostPercentage) {
        this.foodCostPercentage = foodCostPercentage;
    }

    public BigDecimal getLaborCostPercentage() {
        return laborCostPercentage;
    }

    public void setLaborCostPercentage(BigDecimal laborCostPercentage) {
        this.laborCostPercentage = laborCostPercentage;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
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
