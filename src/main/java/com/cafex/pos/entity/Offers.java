package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "offer_applicable_items", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "item_id")
    private List<String> applicableItems;

    @ElementCollection
    @CollectionTable(name = "offer_applicable_categories", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "category_id")
    private List<String> applicableCategories;

    @ElementCollection
    @CollectionTable(name = "offer_applicable_customers", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "customer_id")
    private List<String> applicableCustomers;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

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

    public List<String> getApplicableItems() {
        return applicableItems;
    }

    public void setApplicableItems(List<String> applicableItems) {
        this.applicableItems = applicableItems;
    }

    public List<String> getApplicableCategories() {
        return applicableCategories;
    }

    public void setApplicableCategories(List<String> applicableCategories) {
        this.applicableCategories = applicableCategories;
    }

    public List<String> getApplicableCustomers() {
        return applicableCustomers;
    }

    public void setApplicableCustomers(List<String> applicableCustomers) {
        this.applicableCustomers = applicableCustomers;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
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

    public boolean appliesToItem(String itemId) {
        return applicableItems == null || applicableItems.isEmpty() || applicableItems.contains(itemId);
    }

    public boolean appliesToCategory(String categoryId) {
        return applicableCategories == null || applicableCategories.isEmpty() || applicableCategories.contains(categoryId);
    }

    public boolean appliesToCustomer(String customerId) {
        return applicableCustomers == null || applicableCustomers.isEmpty() || applicableCustomers.contains(customerId);
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
