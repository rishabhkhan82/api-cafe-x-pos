package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shift_reports")
public class ShiftReports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false, unique = true)
    private String reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftSchedules shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "shift_name", nullable = false)
    private String shiftName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "orders_processed", nullable = false)
    private Integer ordersProcessed = 0;

    @Column(name = "revenue", nullable = false)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "cash_sales", nullable = false)
    private BigDecimal cashSales = BigDecimal.ZERO;

    @Column(name = "card_sales", nullable = false)
    private BigDecimal cardSales = BigDecimal.ZERO;

    @Column(name = "upi_sales", nullable = false)
    private BigDecimal upiSales = BigDecimal.ZERO;

    @Column(name = "opening_cash", nullable = false)
    private BigDecimal openingCash = BigDecimal.ZERO;

    @Column(name = "cash_received", nullable = false)
    private BigDecimal cashReceived = BigDecimal.ZERO;

    @Column(name = "cash_paid_out", nullable = false)
    private BigDecimal cashPaidOut = BigDecimal.ZERO;

    @Column(name = "expected_cash", nullable = false)
    private BigDecimal expectedCash = BigDecimal.ZERO;

    @Column(name = "actual_cash", nullable = false)
    private BigDecimal actualCash = BigDecimal.ZERO;

    @Column(name = "cash_difference", nullable = false)
    private BigDecimal cashDifference = BigDecimal.ZERO;

    @Column(name = "voided_items", nullable = false)
    private Integer voidedItems = 0;

    @Column(name = "voided_amount", nullable = false)
    private BigDecimal voidedAmount = BigDecimal.ZERO;

    @Column(name = "refunds_processed", nullable = false)
    private Integer refundsProcessed = 0;

    @Column(name = "refund_amount", nullable = false)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "customer_complaints", nullable = false)
    private Integer customerComplaints = 0;

    @ElementCollection
    @CollectionTable(name = "shift_report_incidents", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "incident")
    private List<String> incidents;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "manager_approval", nullable = false)
    private Boolean managerApproval = false;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ShiftReports() {}

    public ShiftReports(String reportId, ShiftSchedules shift, User staff, LocalDateTime date, String shiftName, LocalDateTime startTime, LocalDateTime endTime) {
        this.reportId = reportId;
        this.shift = shift;
        this.staff = staff;
        this.date = date;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ordersProcessed = 0;
        this.revenue = BigDecimal.ZERO;
        this.cashSales = BigDecimal.ZERO;
        this.cardSales = BigDecimal.ZERO;
        this.upiSales = BigDecimal.ZERO;
        this.openingCash = BigDecimal.ZERO;
        this.cashReceived = BigDecimal.ZERO;
        this.cashPaidOut = BigDecimal.ZERO;
        this.expectedCash = BigDecimal.ZERO;
        this.actualCash = BigDecimal.ZERO;
        this.cashDifference = BigDecimal.ZERO;
        this.voidedItems = 0;
        this.voidedAmount = BigDecimal.ZERO;
        this.refundsProcessed = 0;
        this.refundAmount = BigDecimal.ZERO;
        this.customerComplaints = 0;
        this.managerApproval = false;
        this.generatedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public ShiftSchedules getShift() {
        return shift;
    }

    public void setShift(ShiftSchedules shift) {
        this.shift = shift;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getOrdersProcessed() {
        return ordersProcessed;
    }

    public void setOrdersProcessed(Integer ordersProcessed) {
        this.ordersProcessed = ordersProcessed;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getCashSales() {
        return cashSales;
    }

    public void setCashSales(BigDecimal cashSales) {
        this.cashSales = cashSales;
    }

    public BigDecimal getCardSales() {
        return cardSales;
    }

    public void setCardSales(BigDecimal cardSales) {
        this.cardSales = cardSales;
    }

    public BigDecimal getUpiSales() {
        return upiSales;
    }

    public void setUpiSales(BigDecimal upiSales) {
        this.upiSales = upiSales;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public BigDecimal getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(BigDecimal cashReceived) {
        this.cashReceived = cashReceived;
    }

    public BigDecimal getCashPaidOut() {
        return cashPaidOut;
    }

    public void setCashPaidOut(BigDecimal cashPaidOut) {
        this.cashPaidOut = cashPaidOut;
    }

    public BigDecimal getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(BigDecimal expectedCash) {
        this.expectedCash = expectedCash;
    }

    public BigDecimal getActualCash() {
        return actualCash;
    }

    public void setActualCash(BigDecimal actualCash) {
        this.actualCash = actualCash;
    }

    public BigDecimal getCashDifference() {
        return cashDifference;
    }

    public void setCashDifference(BigDecimal cashDifference) {
        this.cashDifference = cashDifference;
    }

    public Integer getVoidedItems() {
        return voidedItems;
    }

    public void setVoidedItems(Integer voidedItems) {
        this.voidedItems = voidedItems;
    }

    public BigDecimal getVoidedAmount() {
        return voidedAmount;
    }

    public void setVoidedAmount(BigDecimal voidedAmount) {
        this.voidedAmount = voidedAmount;
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

    public Integer getCustomerComplaints() {
        return customerComplaints;
    }

    public void setCustomerComplaints(Integer customerComplaints) {
        this.customerComplaints = customerComplaints;
    }

    public List<String> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<String> incidents) {
        this.incidents = incidents;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getManagerApproval() {
        return managerApproval;
    }

    public void setManagerApproval(Boolean managerApproval) {
        this.managerApproval = managerApproval;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
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
}
