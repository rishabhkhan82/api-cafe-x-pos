package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "broadcast_messages")
public class BroadcastMessages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "target_audience", nullable = false)
    private String targetAudience;

    @ElementCollection
    @CollectionTable(name = "broadcast_target_restaurants", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "restaurant_id")
    private List<String> targetRestaurantIds;

    @ElementCollection
    @CollectionTable(name = "broadcast_target_users", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "user_id")
    private List<String> targetUserIds;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "show_on_login", nullable = false)
    private Boolean showOnLogin = false;

    @Column(name = "show_on_dashboard", nullable = false)
    private Boolean showOnDashboard = false;

    @Column(name = "dismissible", nullable = false)
    private Boolean dismissible = true;

    @ElementCollection
    @CollectionTable(name = "broadcast_dismissed_by", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "user_id")
    private List<String> dismissedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_by")
    private User sentBy;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public BroadcastMessages() {}

    public BroadcastMessages(String messageId, String title, String message, String type, String priority, String targetAudience, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime sentAt) {
        this.messageId = messageId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.targetAudience = targetAudience;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sentAt = sentAt;
        this.isActive = true;
        this.showOnLogin = false;
        this.showOnDashboard = false;
        this.dismissible = true;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public List<String> getTargetRestaurantIds() {
        return targetRestaurantIds;
    }

    public void setTargetRestaurantIds(List<String> targetRestaurantIds) {
        this.targetRestaurantIds = targetRestaurantIds;
    }

    public List<String> getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(List<String> targetUserIds) {
        this.targetUserIds = targetUserIds;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getShowOnLogin() {
        return showOnLogin;
    }

    public void setShowOnLogin(Boolean showOnLogin) {
        this.showOnLogin = showOnLogin;
    }

    public Boolean getShowOnDashboard() {
        return showOnDashboard;
    }

    public void setShowOnDashboard(Boolean showOnDashboard) {
        this.showOnDashboard = showOnDashboard;
    }

    public Boolean getDismissible() {
        return dismissible;
    }

    public void setDismissible(Boolean dismissible) {
        this.dismissible = dismissible;
    }

    public List<String> getDismissedBy() {
        return dismissedBy;
    }

    public void setDismissedBy(List<String> dismissedBy) {
        this.dismissedBy = dismissedBy;
    }

    public User getSentBy() {
        return sentBy;
    }

    public void setSentBy(User sentBy) {
        this.sentBy = sentBy;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
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
}
