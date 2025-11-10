package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipt_logs")
public class ReceiptLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_log_id", nullable = false, unique = true)
    private String receiptLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "receipt_number", nullable = false, unique = true)
    private String receiptNumber;

    @Column(name = "receipt_type", nullable = false)
    private String receiptType;

    @Column(name = "print_format", nullable = false)
    private String printFormat;

    @Column(name = "printer_id")
    private String printerId;

    @Column(name = "printer_name")
    private String printerName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "print_status", nullable = false)
    private String printStatus;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "print_attempts", nullable = false)
    private Integer printAttempts = 1;

    @Column(name = "paper_size")
    private String paperSize;

    @Column(name = "template_used")
    private String templateUsed;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "tax_amount", nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "digital_copy_url")
    private String digitalCopyUrl;

    @Column(name = "ip_address")
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ReceiptLogs() {}

    public ReceiptLogs(String receiptLogId, Order order, String receiptNumber, String receiptType, String printFormat, User cashier, BigDecimal totalAmount, Restaurant restaurant) {
        this.receiptLogId = receiptLogId;
        this.order = order;
        this.receiptNumber = receiptNumber;
        this.receiptType = receiptType;
        this.printFormat = printFormat;
        this.cashier = cashier;
        this.totalAmount = totalAmount;
        this.restaurant = restaurant;
        this.printStatus = "pending";
        this.printAttempts = 1;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptLogId() {
        return receiptLogId;
    }

    public void setReceiptLogId(String receiptLogId) {
        this.receiptLogId = receiptLogId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public String getPrintFormat() {
        return printFormat;
    }

    public void setPrintFormat(String printFormat) {
        this.printFormat = printFormat;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Integer getPrintAttempts() {
        return printAttempts;
    }

    public void setPrintAttempts(Integer printAttempts) {
        this.printAttempts = printAttempts;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public String getTemplateUsed() {
        return templateUsed;
    }

    public void setTemplateUsed(String templateUsed) {
        this.templateUsed = templateUsed;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getDigitalCopyUrl() {
        return digitalCopyUrl;
    }

    public void setDigitalCopyUrl(String digitalCopyUrl) {
        this.digitalCopyUrl = digitalCopyUrl;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
    public boolean isOriginal() {
        return "original".equals(receiptType);
    }

    public boolean isDuplicate() {
        return "duplicate".equals(receiptType);
    }

    public boolean isReprint() {
        return "reprint".equals(receiptType);
    }

    public boolean isRefund() {
        return "refund".equals(receiptType);
    }

    public boolean isThermal() {
        return "thermal".equals(printFormat);
    }

    public boolean isA4() {
        return "a4".equals(printFormat);
    }

    public boolean isDigital() {
        return "digital".equals(printFormat);
    }

    public boolean isSuccessful() {
        return "success".equals(printStatus);
    }

    public boolean isFailed() {
        return "failed".equals(printStatus);
    }

    public boolean isPending() {
        return "pending".equals(printStatus);
    }

    public void markAsSuccessful() {
        this.printStatus = "success";
        this.failureReason = null;
    }

    public void markAsFailed(String reason) {
        this.printStatus = "failed";
        this.failureReason = reason;
        this.printAttempts++;
    }

    public void incrementPrintAttempts() {
        this.printAttempts++;
    }

    public boolean canRetry() {
        return isFailed() && printAttempts < 3; // Max 3 attempts
    }

    public BigDecimal getNetAmount() {
        return totalAmount.add(taxAmount).subtract(discountAmount);
    }
}
