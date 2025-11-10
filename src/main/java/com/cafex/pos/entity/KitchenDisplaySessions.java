package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kitchen_display_sessions")
public class KitchenDisplaySessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private User operator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "station_id")
    private String stationId;

    @Column(name = "session_start", nullable = false)
    private LocalDateTime sessionStart;

    @Column(name = "session_end")
    private LocalDateTime sessionEnd;

    @Column(name = "session_duration")
    private Integer sessionDuration;

    @Column(name = "orders_viewed", nullable = false)
    private Integer ordersViewed = 0;

    @Column(name = "orders_updated", nullable = false)
    private Integer ordersUpdated = 0;

    @Column(name = "orders_completed", nullable = false)
    private Integer ordersCompleted = 0;

    @Column(name = "average_response_time")
    private BigDecimal averageResponseTime;

    @Column(name = "device_info", columnDefinition = "JSON")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "screen_resolution")
    private String screenResolution;

    @Column(name = "orders_by_hour", columnDefinition = "JSON")
    private String ordersByHour;

    @Column(name = "peak_hours", columnDefinition = "JSON")
    private List<String> peakHours;

    @Column(name = "bottlenecks", columnDefinition = "JSON")
    private List<String> bottlenecks;

    @Column(name = "session_notes", columnDefinition = "TEXT")
    private String sessionNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public KitchenDisplaySessions() {}

    public KitchenDisplaySessions(String sessionId, User operator, Restaurant restaurant, LocalDateTime sessionStart) {
        this.sessionId = sessionId;
        this.operator = operator;
        this.restaurant = restaurant;
        this.sessionStart = sessionStart;
        this.ordersViewed = 0;
        this.ordersUpdated = 0;
        this.ordersCompleted = 0;
        this.createdAt = LocalDateTime.now();
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

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(LocalDateTime sessionStart) {
        this.sessionStart = sessionStart;
    }

    public LocalDateTime getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(LocalDateTime sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public Integer getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Integer sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public Integer getOrdersViewed() {
        return ordersViewed;
    }

    public void setOrdersViewed(Integer ordersViewed) {
        this.ordersViewed = ordersViewed;
    }

    public Integer getOrdersUpdated() {
        return ordersUpdated;
    }

    public void setOrdersUpdated(Integer ordersUpdated) {
        this.ordersUpdated = ordersUpdated;
    }

    public Integer getOrdersCompleted() {
        return ordersCompleted;
    }

    public void setOrdersCompleted(Integer ordersCompleted) {
        this.ordersCompleted = ordersCompleted;
    }

    public BigDecimal getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(BigDecimal averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    public String getOrdersByHour() {
        return ordersByHour;
    }

    public void setOrdersByHour(String ordersByHour) {
        this.ordersByHour = ordersByHour;
    }

    public List<String> getPeakHours() {
        return peakHours;
    }

    public void setPeakHours(List<String> peakHours) {
        this.peakHours = peakHours;
    }

    public List<String> getBottlenecks() {
        return bottlenecks;
    }

    public void setBottlenecks(List<String> bottlenecks) {
        this.bottlenecks = bottlenecks;
    }

    public String getSessionNotes() {
        return sessionNotes;
    }

    public void setSessionNotes(String sessionNotes) {
        this.sessionNotes = sessionNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
