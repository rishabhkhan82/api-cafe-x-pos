package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_configurations")
public class NotificationConfigurations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = false;

    @Column(name = "sms_notifications", nullable = false)
    private Boolean smsNotifications = false;

    @Column(name = "push_notifications", nullable = false)
    private Boolean pushNotifications = false;

    @Column(name = "order_alerts", nullable = false)
    private Boolean orderAlerts = false;

    @Column(name = "payment_alerts", nullable = false)
    private Boolean paymentAlerts = false;

    @Column(name = "low_stock_alerts", nullable = false)
    private Boolean lowStockAlerts = false;

    @Column(name = "staff_alerts", nullable = false)
    private Boolean staffAlerts = false;

    @Column(name = "sms_provider")
    private String smsProvider;

    @Column(name = "sms_api_key")
    private String smsApiKey;

    @Column(name = "push_server_key")
    private String pushServerKey;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public NotificationConfigurations() {}

    public NotificationConfigurations(Boolean emailNotifications, Boolean smsNotifications,
                                    Boolean pushNotifications, Boolean orderAlerts,
                                    Boolean paymentAlerts, Boolean lowStockAlerts, Boolean staffAlerts) {
        this.emailNotifications = emailNotifications != null ? emailNotifications : false;
        this.smsNotifications = smsNotifications != null ? smsNotifications : false;
        this.pushNotifications = pushNotifications != null ? pushNotifications : false;
        this.orderAlerts = orderAlerts != null ? orderAlerts : false;
        this.paymentAlerts = paymentAlerts != null ? paymentAlerts : false;
        this.lowStockAlerts = lowStockAlerts != null ? lowStockAlerts : false;
        this.staffAlerts = staffAlerts != null ? staffAlerts : false;
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

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Boolean getOrderAlerts() {
        return orderAlerts;
    }

    public void setOrderAlerts(Boolean orderAlerts) {
        this.orderAlerts = orderAlerts;
    }

    public Boolean getPaymentAlerts() {
        return paymentAlerts;
    }

    public void setPaymentAlerts(Boolean paymentAlerts) {
        this.paymentAlerts = paymentAlerts;
    }

    public Boolean getLowStockAlerts() {
        return lowStockAlerts;
    }

    public void setLowStockAlerts(Boolean lowStockAlerts) {
        this.lowStockAlerts = lowStockAlerts;
    }

    public Boolean getStaffAlerts() {
        return staffAlerts;
    }

    public void setStaffAlerts(Boolean staffAlerts) {
        this.staffAlerts = staffAlerts;
    }

    public String getSmsProvider() {
        return smsProvider;
    }

    public void setSmsProvider(String smsProvider) {
        this.smsProvider = smsProvider;
    }

    public String getSmsApiKey() {
        return smsApiKey;
    }

    public void setSmsApiKey(String smsApiKey) {
        this.smsApiKey = smsApiKey;
    }

    public String getPushServerKey() {
        return pushServerKey;
    }

    public void setPushServerKey(String pushServerKey) {
        this.pushServerKey = pushServerKey;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
