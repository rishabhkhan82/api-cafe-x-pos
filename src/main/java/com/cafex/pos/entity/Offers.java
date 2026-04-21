package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
public class Offers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offer_id", nullable = false, unique = true)
    private String offerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String type;

    @Column
    private BigDecimal value;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "min_order_value")
    private BigDecimal minOrderValue;



    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit = 0;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @Column(name = "max_usage_per_customer", nullable = false)
    private Integer maxUsagePerCustomer = 1;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "auto_apply", nullable = false)
    private Boolean autoApply = false;

    @Column
    private String code;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Offers() {}

    public Offers(String offerId, String name, String title, String type, LocalDateTime startDate, LocalDateTime endDate) {
        this.offerId = offerId;
        this.name = name;
        this.title = title;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
        this.autoApply = false;
        this.usageLimit = 0;
        this.usageCount = 0;
        this.maxUsagePerCustomer = 1;
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

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }



    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Integer getMaxUsagePerCustomer() {
        return maxUsagePerCustomer;
    }

    public void setMaxUsagePerCustomer(Integer maxUsagePerCustomer) {
        this.maxUsagePerCustomer = maxUsagePerCustomer;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getAutoApply() {
        return autoApply;
    }

    public void setAutoApply(Boolean autoApply) {
        this.autoApply = autoApply;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
    public boolean isPercentage() {
        return "percentage".equals(type);
    }

    public boolean isFixed() {
        return "fixed".equals(type);
    }

    public boolean isBuyOneGetOne() {
        return "buy_one_get_one".equals(type);
    }

    public boolean isFreeItem() {
        return "free_item".equals(type);
    }

    public boolean isLoyalty() {
        return "loyalty".equals(type);
    }

    public boolean isActiveNow() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && now.isAfter(startDate) && now.isBefore(endDate);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public boolean isUpcoming() {
        return LocalDateTime.now().isBefore(startDate);
    }

    public boolean hasUsageLimit() {
        return usageLimit > 0;
    }

    public boolean isUsageLimitReached() {
        return hasUsageLimit() && usageCount >= usageLimit;
    }

    public boolean canBeUsed() {
        return isActiveNow() && !isUsageLimitReached();
    }

    public void incrementUsage() {
        this.usageCount++;
        this.updatedAt = LocalDateTime.now();
    }



    public boolean hasCode() {
        return code != null && !code.trim().isEmpty();
    }

    public boolean isCodeValid(String inputCode) {
        return hasCode() && code.equalsIgnoreCase(inputCode);
    }

    public long getDaysRemaining() {
        return java.time.Duration.between(LocalDateTime.now(), endDate).toDays();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void extendEndDate(LocalDateTime newEndDate) {
        this.endDate = newEndDate;
        this.updatedAt = LocalDateTime.now();
    }
}
