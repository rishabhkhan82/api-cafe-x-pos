package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_transactions")
public class CashTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drawer_id", nullable = false)
    private CashDrawerOperations drawer;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String description;

    @Column(name = "reference")
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approval_required", nullable = false)
    private Boolean approvalRequired = false;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Column(name = "balance_before", nullable = false)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "is_voided", nullable = false)
    private Boolean isVoided = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voided_by")
    private User voidedBy;

    @Column(name = "voided_at")
    private LocalDateTime voidedAt;

    @Column(name = "void_reason")
    private String voidReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public CashTransactions() {}

    public CashTransactions(String transactionId, CashDrawerOperations drawer, String transactionType, BigDecimal amount, User performedBy, BigDecimal balanceBefore, Restaurant restaurant) {
        this.transactionId = transactionId;
        this.drawer = drawer;
        this.transactionType = transactionType;
        this.amount = amount;
        this.performedBy = performedBy;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceBefore.add(amount);
        this.restaurant = restaurant;
        this.isVoided = false;
        this.approvalRequired = requiresApproval(transactionType);
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public CashDrawerOperations getDrawer() {
        return drawer;
    }

    public void setDrawer(CashDrawerOperations drawer) {
        this.drawer = drawer;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
        this.approvalRequired = requiresApproval(transactionType);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        if (this.balanceBefore != null) {
            this.balanceAfter = this.balanceBefore.add(amount);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Boolean getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(Boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
        if (this.amount != null) {
            this.balanceAfter = balanceBefore.add(amount);
        }
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Boolean getIsVoided() {
        return isVoided;
    }

    public void setIsVoided(Boolean isVoided) {
        this.isVoided = isVoided;
    }

    public User getVoidedBy() {
        return voidedBy;
    }

    public void setVoidedBy(User voidedBy) {
        this.voidedBy = voidedBy;
    }

    public LocalDateTime getVoidedAt() {
        return voidedAt;
    }

    public void setVoidedAt(LocalDateTime voidedAt) {
        this.voidedAt = voidedAt;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
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

    // Helper methods
    public boolean isDeposit() {
        return "deposit".equals(transactionType);
    }

    public boolean isWithdrawal() {
        return "withdrawal".equals(transactionType);
    }

    public boolean isSale() {
        return "sale".equals(transactionType);
    }

    public boolean isRefund() {
        return "refund".equals(transactionType);
    }

    public boolean isFloatStart() {
        return "float_start".equals(transactionType);
    }

    public boolean isFloatEnd() {
        return "float_end".equals(transactionType);
    }

    public boolean isSafeDeposit() {
        return "safe_deposit".equals(transactionType);
    }

    public boolean isChangeFund() {
        return "change_fund".equals(transactionType);
    }

    public void voidTransaction(User voidedBy, String reason) {
        this.isVoided = true;
        this.voidedBy = voidedBy;
        this.voidedAt = LocalDateTime.now();
        this.voidReason = reason;
    }

    public void approve(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public boolean isApproved() {
        return !approvalRequired || approvedBy != null;
    }

    private boolean requiresApproval(String transactionType) {
        // Define which transaction types require approval
        return "withdrawal".equals(transactionType) ||
               "safe_deposit".equals(transactionType) ||
               "change_fund".equals(transactionType);
    }

    public boolean isPositiveAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegativeAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
}
