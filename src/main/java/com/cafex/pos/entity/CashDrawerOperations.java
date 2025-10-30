package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_drawer_operations")
public class CashDrawerOperations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_id", nullable = false, unique = true)
    private String operationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @Column(name = "drawer_id", nullable = false, unique = true)
    private String drawerId;

    @Column(nullable = false)
    private String status;

    @Column(name = "starting_float", nullable = false)
    private BigDecimal startingFloat = BigDecimal.ZERO;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "expected_balance", nullable = false)
    private BigDecimal expectedBalance = BigDecimal.ZERO;

    @Column(name = "variance", nullable = false)
    private BigDecimal variance = BigDecimal.ZERO;

    @Column(name = "last_reconciled")
    private LocalDateTime lastReconciled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconciled_by")
    private User reconciledBy;

    @Column(name = "shift_id")
    private String shiftId;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public CashDrawerOperations() {}

    public CashDrawerOperations(String operationId, Restaurant restaurant, User cashier, String drawerId, BigDecimal startingFloat) {
        this.operationId = operationId;
        this.restaurant = restaurant;
        this.cashier = cashier;
        this.drawerId = drawerId;
        this.status = "active";
        this.startingFloat = startingFloat != null ? startingFloat : BigDecimal.ZERO;
        this.currentBalance = this.startingFloat;
        this.expectedBalance = this.startingFloat;
        this.variance = BigDecimal.ZERO;
        this.openedAt = LocalDateTime.now();
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

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }

    public String getDrawerId() {
        return drawerId;
    }

    public void setDrawerId(String drawerId) {
        this.drawerId = drawerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getStartingFloat() {
        return startingFloat;
    }

    public void setStartingFloat(BigDecimal startingFloat) {
        this.startingFloat = startingFloat;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
        this.calculateVariance();
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getExpectedBalance() {
        return expectedBalance;
    }

    public void setExpectedBalance(BigDecimal expectedBalance) {
        this.expectedBalance = expectedBalance;
        this.calculateVariance();
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getVariance() {
        return variance;
    }

    public void setVariance(BigDecimal variance) {
        this.variance = variance;
    }

    public LocalDateTime getLastReconciled() {
        return lastReconciled;
    }

    public void setLastReconciled(LocalDateTime lastReconciled) {
        this.lastReconciled = lastReconciled;
    }

    public User getReconciledBy() {
        return reconciledBy;
    }

    public void setReconciledBy(User reconciledBy) {
        this.reconciledBy = reconciledBy;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
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
        return "active".equals(status);
    }

    public boolean isReconciled() {
        return "reconciled".equals(status);
    }

    public boolean isClosed() {
        return "closed".equals(status);
    }

    public boolean isSuspended() {
        return "suspended".equals(status);
    }

    public void reconcile(User reconciledBy) {
        this.status = "reconciled";
        this.lastReconciled = LocalDateTime.now();
        this.reconciledBy = reconciledBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = "closed";
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = "suspended";
        this.updatedAt = LocalDateTime.now();
    }

    private void calculateVariance() {
        this.variance = this.currentBalance.subtract(this.expectedBalance);
    }

    public boolean hasVariance() {
        return variance.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean isOver() {
        return variance.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isShort() {
        return variance.compareTo(BigDecimal.ZERO) < 0;
    }
}
