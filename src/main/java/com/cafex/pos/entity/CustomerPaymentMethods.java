package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_payment_methods")
public class CustomerPaymentMethods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_method_id", nullable = false, unique = true)
    private String paymentMethodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "provider")
    private String provider;

    @Column(name = "account_identifier", nullable = false)
    private String accountIdentifier;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "expiry_month")
    private Integer expiryMonth;

    @Column(name = "expiry_year")
    private Integer expiryYear;

    @Column(name = "cardholder_name")
    private String cardholderName;

    @Column(name = "billing_address_id")
    private String billingAddressId;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts = 0;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public CustomerPaymentMethods() {}

    public CustomerPaymentMethods(String paymentMethodId, Customer customer, String paymentType, String accountIdentifier) {
        this.paymentMethodId = paymentMethodId;
        this.customer = customer;
        this.paymentType = paymentType;
        this.accountIdentifier = accountIdentifier;
        this.isActive = true;
        this.isDefault = false;
        this.failedAttempts = 0;
        this.verificationStatus = "pending";
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(String billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
    public boolean isCard() {
        return "card".equals(paymentType);
    }

    public boolean isUpi() {
        return "upi".equals(paymentType);
    }

    public boolean isWallet() {
        return "wallet".equals(paymentType);
    }

    public boolean isNetBanking() {
        return "net_banking".equals(paymentType);
    }

    public boolean isVerified() {
        return "verified".equals(verificationStatus);
    }

    public boolean isPending() {
        return "pending".equals(verificationStatus);
    }

    public boolean isFailed() {
        return "failed".equals(verificationStatus);
    }

    public boolean isExpired() {
        if (expiryYear == null || expiryMonth == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        if (expiryYear < currentYear) {
            return true;
        } else if (expiryYear == currentYear) {
            return expiryMonth < currentMonth;
        }
        return false;
    }

    public void recordSuccessfulUse() {
        this.lastUsed = LocalDateTime.now();
        this.failedAttempts = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordFailedAttempt() {
        this.failedAttempts++;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsVerified() {
        this.verificationStatus = "verified";
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.verificationStatus = "failed";
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAsDefault() {
        this.isDefault = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeAsDefault() {
        this.isDefault = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean shouldBlock() {
        return failedAttempts >= 3; // Block after 3 failed attempts
    }

    public String getMaskedIdentifier() {
        if (accountIdentifier == null || accountIdentifier.length() < 4) {
            return accountIdentifier;
        }

        if (isCard()) {
            // Show last 4 digits for cards
            return "**** **** **** " + accountIdentifier.substring(accountIdentifier.length() - 4);
        } else {
            // Show last 4 characters for other payment types
            return "****" + accountIdentifier.substring(accountIdentifier.length() - 4);
        }
    }
}
