package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_gateways")
public class PaymentGateways {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gateway_id", nullable = false, unique = true)
    private String gatewayId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String provider;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String icon;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "api_secret")
    private String apiSecret;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "webhook_secret")
    private String webhookSecret;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_test_mode", nullable = false)
    private Boolean isTestMode = false;

    @ElementCollection
    @CollectionTable(name = "payment_gateway_currencies", joinColumns = @JoinColumn(name = "gateway_id"))
    @Column(name = "currency")
    private java.util.List<String> supportedCurrencies;

    @ElementCollection
    @CollectionTable(name = "payment_gateway_methods", joinColumns = @JoinColumn(name = "gateway_id"))
    @Column(name = "method")
    private java.util.List<String> supportedMethods;

    @Column(name = "transaction_fee")
    private BigDecimal transactionFee;

    @Column(name = "fixed_fee")
    private BigDecimal fixedFee;

    @Column(name = "settlement_days", nullable = false)
    private Integer settlementDays = 1;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @Column(name = "daily_limit")
    private BigDecimal dailyLimit;

    @Column(name = "monthly_limit")
    private BigDecimal monthlyLimit;

    @Column(name = "success_rate")
    private BigDecimal successRate;

    @Column(name = "average_processing_time", nullable = false)
    private Integer averageProcessingTime = 0;

    @Column(name = "last_tested")
    private LocalDateTime lastTested;

    @Column(name = "test_result")
    private String testResult;

    @Column(name = "restaurant_id")
    private String restaurantId;

    @Column(name = "configured_by")
    private String configuredBy;

    @Column(name = "configured_at", nullable = false)
    private LocalDateTime configuredAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public PaymentGateways() {}

    public PaymentGateways(String gatewayId, String name, String provider) {
        this.gatewayId = gatewayId;
        this.name = name;
        this.provider = provider;
        this.configuredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsTestMode() {
        return isTestMode;
    }

    public void setIsTestMode(Boolean isTestMode) {
        this.isTestMode = isTestMode;
    }

    public java.util.List<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public void setSupportedCurrencies(java.util.List<String> supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }

    public java.util.List<String> getSupportedMethods() {
        return supportedMethods;
    }

    public void setSupportedMethods(java.util.List<String> supportedMethods) {
        this.supportedMethods = supportedMethods;
    }

    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public BigDecimal getFixedFee() {
        return fixedFee;
    }

    public void setFixedFee(BigDecimal fixedFee) {
        this.fixedFee = fixedFee;
    }

    public Integer getSettlementDays() {
        return settlementDays;
    }

    public void setSettlementDays(Integer settlementDays) {
        this.settlementDays = settlementDays;
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

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public Integer getAverageProcessingTime() {
        return averageProcessingTime;
    }

    public void setAverageProcessingTime(Integer averageProcessingTime) {
        this.averageProcessingTime = averageProcessingTime;
    }

    public LocalDateTime getLastTested() {
        return lastTested;
    }

    public void setLastTested(LocalDateTime lastTested) {
        this.lastTested = lastTested;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getConfiguredBy() {
        return configuredBy;
    }

    public void setConfiguredBy(String configuredBy) {
        this.configuredBy = configuredBy;
    }

    public LocalDateTime getConfiguredAt() {
        return configuredAt;
    }

    public void setConfiguredAt(LocalDateTime configuredAt) {
        this.configuredAt = configuredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
