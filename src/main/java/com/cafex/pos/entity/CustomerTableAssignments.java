package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_table_assignments")
public class CustomerTableAssignments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false, unique = true)
    private String assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "table_id", nullable = false)
    private String tableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "assignment_type", nullable = false)
    private String assignmentType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "party_size", nullable = false)
    private Integer partySize = 1;

    @Column(name = "waiter_id")
    private String waiterId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "total_spent")
    private BigDecimal totalSpent;

    @Column(name = "average_spend_per_person")
    private BigDecimal averageSpendPerPerson;

    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating;

    @Column(name = "revisit_likelihood")
    private Integer revisitLikelihood;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @Column(name = "accessibility_needs", columnDefinition = "TEXT")
    private String accessibilityNeeds;

    @ElementCollection
    @CollectionTable(name = "customer_table_dietary_restrictions", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "restriction")
    private List<String> dietaryRestrictions;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Column(name = "vip_status", nullable = false)
    private Boolean vipStatus = false;

    @Column(name = "loyalty_points_earned")
    private Integer loyaltyPointsEarned;

    @Column(name = "feedback_provided", nullable = false)
    private Boolean feedbackProvided = false;

    @Column(name = "next_visit_scheduled")
    private LocalDateTime nextVisitScheduled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public CustomerTableAssignments() {}

    public CustomerTableAssignments(String assignmentId, Customer customer, String tableId, Restaurant restaurant, String assignmentType, LocalDateTime startTime, Integer partySize, String status) {
        this.assignmentId = assignmentId;
        this.customer = customer;
        this.tableId = tableId;
        this.restaurant = restaurant;
        this.assignmentType = assignmentType;
        this.startTime = startTime;
        this.partySize = partySize;
        this.status = status;
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

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getPartySize() {
        return partySize;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public String getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(String waiterId) {
        this.waiterId = waiterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getAverageSpendPerPerson() {
        return averageSpendPerPerson;
    }

    public void setAverageSpendPerPerson(BigDecimal averageSpendPerPerson) {
        this.averageSpendPerPerson = averageSpendPerPerson;
    }

    public Integer getSatisfactionRating() {
        return satisfactionRating;
    }

    public void setSatisfactionRating(Integer satisfactionRating) {
        this.satisfactionRating = satisfactionRating;
    }

    public Integer getRevisitLikelihood() {
        return revisitLikelihood;
    }

    public void setRevisitLikelihood(Integer revisitLikelihood) {
        this.revisitLikelihood = revisitLikelihood;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getAccessibilityNeeds() {
        return accessibilityNeeds;
    }

    public void setAccessibilityNeeds(String accessibilityNeeds) {
        this.accessibilityNeeds = accessibilityNeeds;
    }

    public List<String> getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(List<String> dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Boolean getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(Boolean vipStatus) {
        this.vipStatus = vipStatus;
    }

    public Integer getLoyaltyPointsEarned() {
        return loyaltyPointsEarned;
    }

    public void setLoyaltyPointsEarned(Integer loyaltyPointsEarned) {
        this.loyaltyPointsEarned = loyaltyPointsEarned;
    }

    public Boolean getFeedbackProvided() {
        return feedbackProvided;
    }

    public void setFeedbackProvided(Boolean feedbackProvided) {
        this.feedbackProvided = feedbackProvided;
    }

    public LocalDateTime getNextVisitScheduled() {
        return nextVisitScheduled;
    }

    public void setNextVisitScheduled(LocalDateTime nextVisitScheduled) {
        this.nextVisitScheduled = nextVisitScheduled;
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
