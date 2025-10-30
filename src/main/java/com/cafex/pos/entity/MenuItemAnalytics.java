package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_item_analytics")
public class MenuItemAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analytics_id", nullable = false, unique = true)
    private String analyticsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "order_count", nullable = false)
    private Integer orderCount = 0;

    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold = 0;

    @Column(name = "revenue")
    private BigDecimal revenue;

    @Column(name = "average_price")
    private BigDecimal averagePrice;

    @Column(name = "discount_applied")
    private BigDecimal discountApplied;

    @Column(name = "popularity_rank", nullable = false)
    private Integer popularityRank = 0;

    @Column(name = "category_rank", nullable = false)
    private Integer categoryRank = 0;

    @Column(name = "preparation_time")
    private BigDecimal preparationTime;

    @Column(name = "customer_rating")
    private BigDecimal customerRating;

    @Column(name = "reorder_rate")
    private BigDecimal reorderRate;

    @Column(name = "add_on_revenue")
    private BigDecimal addOnRevenue;

    @Column(name = "wastage_amount")
    private BigDecimal wastageAmount;

    @Column(name = "cost_of_goods_sold")
    private BigDecimal costOfGoodsSold;

    @Column(name = "gross_margin")
    private BigDecimal grossMargin;

    @Column(name = "peak_ordering_hour")
    private String peakOrderingHour;

    @Column(name = "peak_ordering_day")
    private String peakOrderingDay;

    @Column(name = "customer_demographics", columnDefinition = "JSON")
    private String customerDemographics;

    @Column(name = "seasonal_trends", columnDefinition = "JSON")
    private String seasonalTrends;

    @Column(name = "competitor_price")
    private BigDecimal competitorPrice;

    @Column(name = "price_elasticity")
    private BigDecimal priceElasticity;

    @Column(name = "demand_forecast")
    private BigDecimal demandForecast;

    @Column(name = "stockout_incidents", nullable = false)
    private Integer stockoutIncidents = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public MenuItemAnalytics() {}

    public MenuItemAnalytics(String analyticsId, MenuItem menuItem, Restaurant restaurant, LocalDateTime date, String period) {
        this.analyticsId = analyticsId;
        this.menuItem = menuItem;
        this.restaurant = restaurant;
        this.date = date;
        this.period = period;
        this.orderCount = 0;
        this.quantitySold = 0;
        this.popularityRank = 0;
        this.categoryRank = 0;
        this.stockoutIncidents = 0;
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

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
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

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(BigDecimal discountApplied) {
        this.discountApplied = discountApplied;
    }

    public Integer getPopularityRank() {
        return popularityRank;
    }

    public void setPopularityRank(Integer popularityRank) {
        this.popularityRank = popularityRank;
    }

    public Integer getCategoryRank() {
        return categoryRank;
    }

    public void setCategoryRank(Integer categoryRank) {
        this.categoryRank = categoryRank;
    }

    public BigDecimal getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(BigDecimal preparationTime) {
        this.preparationTime = preparationTime;
    }

    public BigDecimal getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(BigDecimal customerRating) {
        this.customerRating = customerRating;
    }

    public BigDecimal getReorderRate() {
        return reorderRate;
    }

    public void setReorderRate(BigDecimal reorderRate) {
        this.reorderRate = reorderRate;
    }

    public BigDecimal getAddOnRevenue() {
        return addOnRevenue;
    }

    public void setAddOnRevenue(BigDecimal addOnRevenue) {
        this.addOnRevenue = addOnRevenue;
    }

    public BigDecimal getWastageAmount() {
        return wastageAmount;
    }

    public void setWastageAmount(BigDecimal wastageAmount) {
        this.wastageAmount = wastageAmount;
    }

    public BigDecimal getCostOfGoodsSold() {
        return costOfGoodsSold;
    }

    public void setCostOfGoodsSold(BigDecimal costOfGoodsSold) {
        this.costOfGoodsSold = costOfGoodsSold;
    }

    public BigDecimal getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(BigDecimal grossMargin) {
        this.grossMargin = grossMargin;
    }

    public String getPeakOrderingHour() {
        return peakOrderingHour;
    }

    public void setPeakOrderingHour(String peakOrderingHour) {
        this.peakOrderingHour = peakOrderingHour;
    }

    public String getPeakOrderingDay() {
        return peakOrderingDay;
    }

    public void setPeakOrderingDay(String peakOrderingDay) {
        this.peakOrderingDay = peakOrderingDay;
    }

    public String getCustomerDemographics() {
        return customerDemographics;
    }

    public void setCustomerDemographics(String customerDemographics) {
        this.customerDemographics = customerDemographics;
    }

    public String getSeasonalTrends() {
        return seasonalTrends;
    }

    public void setSeasonalTrends(String seasonalTrends) {
        this.seasonalTrends = seasonalTrends;
    }

    public BigDecimal getCompetitorPrice() {
        return competitorPrice;
    }

    public void setCompetitorPrice(BigDecimal competitorPrice) {
        this.competitorPrice = competitorPrice;
    }

    public BigDecimal getPriceElasticity() {
        return priceElasticity;
    }

    public void setPriceElasticity(BigDecimal priceElasticity) {
        this.priceElasticity = priceElasticity;
    }

    public BigDecimal getDemandForecast() {
        return demandForecast;
    }

    public void setDemandForecast(BigDecimal demandForecast) {
        this.demandForecast = demandForecast;
    }

    public Integer getStockoutIncidents() {
        return stockoutIncidents;
    }

    public void setStockoutIncidents(Integer stockoutIncidents) {
        this.stockoutIncidents = stockoutIncidents;
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
