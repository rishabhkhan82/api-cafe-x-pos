package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cashier_sessions")
public class CashierSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "session_token", nullable = false, unique = true)
    private String sessionToken;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "session_duration", nullable = false)
    private Integer sessionDuration = 0;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Column(name = "location", columnDefinition = "TEXT")
    private String location;

    @Column(name = "drawer_id")
    private String drawerId;

    @Column(name = "shift_id")
    private String shiftId;

    @Column(name = "orders_processed", nullable = false)
    private Integer ordersProcessed = 0;

    @Column(name = "sales_amount", nullable = false)
    private BigDecimal salesAmount = BigDecimal.ZERO;

    @Column(name = "refunds_processed", nullable = false)
    private Integer refundsProcessed = 0;

    @Column(name = "refund_amount", nullable = false)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "voids_processed", nullable = false)
    private Integer voidsProcessed = 0;

    @Column(name = "void_amount", nullable = false)
    private BigDecimal voidAmount = BigDecimal.ZERO;

    @Column(name = "cash_handled", nullable = false)
    private BigDecimal cashHandled = BigDecimal.ZERO;

    @Column(name = "session_status", nullable = false)
    private String sessionStatus;

    @Column(name = "termination_reason")
    private String terminationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminated_by")
    private User terminatedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public CashierSessions() {}

    public CashierSessions(String sessionId, User cashier, Restaurant restaurant, String sessionToken, String ipAddress) {
        this.sessionId = sessionId;
        this.cashier = cashier;
        this.restaurant = restaurant;
        this.sessionToken = sessionToken;
        this.ipAddress = ipAddress;
        this.loginTime = LocalDateTime.now();
        this.sessionStatus = "active";
        this.ordersProcessed = 0;
        this.salesAmount = BigDecimal.ZERO;
        this.refundsProcessed = 0;
        this.refundAmount = BigDecimal.ZERO;
        this.voidsProcessed = 0;
        this.voidAmount = BigDecimal.ZERO;
        this.cashHandled = BigDecimal.ZERO;
        this.sessionDuration = 0;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
        if (logoutTime != null && loginTime != null) {
            this.sessionDuration = (int) java.time.Duration.between(loginTime, logoutTime).toMinutes();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Integer sessionDuration) {
        this.sessionDuration = sessionDuration;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDrawerId() {
        return drawerId;
    }

    public void setDrawerId(String drawerId) {
        this.drawerId = drawerId;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public Integer getOrdersProcessed() {
        return ordersProcessed;
    }

    public void setOrdersProcessed(Integer ordersProcessed) {
        this.ordersProcessed = ordersProcessed;
    }

    public BigDecimal getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }

    public Integer getRefundsProcessed() {
        return refundsProcessed;
    }

    public void setRefundsProcessed(Integer refundsProcessed) {
        this.refundsProcessed = refundsProcessed;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Integer getVoidsProcessed() {
        return voidsProcessed;
    }

    public void setVoidsProcessed(Integer voidsProcessed) {
        this.voidsProcessed = voidsProcessed;
    }

    public BigDecimal getVoidAmount() {
        return voidAmount;
    }

    public void setVoidAmount(BigDecimal voidAmount) {
        this.voidAmount = voidAmount;
    }

    public BigDecimal getCashHandled() {
        return cashHandled;
    }

    public void setCashHandled(BigDecimal cashHandled) {
        this.cashHandled = cashHandled;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public User getTerminatedBy() {
        return terminatedBy;
    }

    public void setTerminatedBy(User terminatedBy) {
        this.terminatedBy = terminatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
    public boolean isActive() {
        return "active".equals(sessionStatus);
    }

    public boolean isCompleted() {
        return "completed".equals(sessionStatus);
    }

    public boolean isTerminated() {
        return "terminated".equals(sessionStatus);
    }

    public boolean isSuspended() {
        return "suspended".equals(sessionStatus);
    }

    public void completeSession() {
        this.sessionStatus = "completed";
        this.logoutTime = LocalDateTime.now();
        this.sessionDuration = (int) java.time.Duration.between(loginTime, logoutTime).toMinutes();
        this.updatedAt = LocalDateTime.now();
    }

    public void terminateSession(User terminatedBy, String reason) {
        this.sessionStatus = "terminated";
        this.terminatedBy = terminatedBy;
        this.terminationReason = reason;
        this.logoutTime = LocalDateTime.now();
        this.sessionDuration = (int) java.time.Duration.between(loginTime, logoutTime).toMinutes();
        this.updatedAt = LocalDateTime.now();
    }

    public void suspendSession() {
        this.sessionStatus = "suspended";
        this.updatedAt = LocalDateTime.now();
    }

    public void recordOrder(BigDecimal amount) {
        this.ordersProcessed++;
        this.salesAmount = this.salesAmount.add(amount);
        this.cashHandled = this.cashHandled.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void recordRefund(BigDecimal amount) {
        this.refundsProcessed++;
        this.refundAmount = this.refundAmount.add(amount);
        this.cashHandled = this.cashHandled.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void recordVoid(BigDecimal amount) {
        this.voidsProcessed++;
        this.voidAmount = this.voidAmount.add(amount);
        this.salesAmount = this.salesAmount.subtract(amount);
        this.cashHandled = this.cashHandled.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getNetSales() {
        return salesAmount.subtract(refundAmount).subtract(voidAmount);
    }

    public BigDecimal getAverageOrderValue() {
        if (ordersProcessed == 0) return BigDecimal.ZERO;
        return salesAmount.divide(BigDecimal.valueOf(ordersProcessed), 2, java.math.RoundingMode.HALF_UP);
    }
}
