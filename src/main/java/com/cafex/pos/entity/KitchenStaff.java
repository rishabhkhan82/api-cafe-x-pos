package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kitchen_staff")
public class KitchenStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_id", nullable = false, unique = true)
    private String staffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "station")
    private String station;

    @Column(name = "shift", nullable = false)
    private String shift;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    @Column(name = "performance_rating")
    private BigDecimal performanceRating;

    @ElementCollection
    @CollectionTable(name = "kitchen_staff_skills", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "skill")
    private List<String> skills;

    @ElementCollection
    @CollectionTable(name = "kitchen_staff_certifications", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "certification")
    private List<String> certifications;

    @Column(name = "emergency_contact", columnDefinition = "JSON")
    private String emergencyContact;

    @Column(name = "work_schedule", columnDefinition = "JSON")
    private String workSchedule;

    @Column(name = "current_task")
    private String currentTask;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @Column(name = "total_orders_prepared", nullable = false)
    private Integer totalOrdersPrepared = 0;

    @Column(name = "average_prep_time")
    private BigDecimal averagePrepTime;

    @Column(name = "quality_rating")
    private BigDecimal qualityRating;

    @Column(name = "attendance_rate")
    private BigDecimal attendanceRate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public KitchenStaff() {}

    public KitchenStaff(String staffId, User user, Restaurant restaurant, String role, String shift) {
        this.staffId = staffId;
        this.user = user;
        this.restaurant = restaurant;
        this.role = role;
        this.shift = shift;
        this.isActive = true;
        this.totalOrdersPrepared = 0;
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

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public BigDecimal getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(BigDecimal performanceRating) {
        this.performanceRating = performanceRating;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }

    public String getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(String currentTask) {
        this.currentTask = currentTask;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public Integer getTotalOrdersPrepared() {
        return totalOrdersPrepared;
    }

    public void setTotalOrdersPrepared(Integer totalOrdersPrepared) {
        this.totalOrdersPrepared = totalOrdersPrepared;
    }

    public BigDecimal getAveragePrepTime() {
        return averagePrepTime;
    }

    public void setAveragePrepTime(BigDecimal averagePrepTime) {
        this.averagePrepTime = averagePrepTime;
    }

    public BigDecimal getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(BigDecimal qualityRating) {
        this.qualityRating = qualityRating;
    }

    public BigDecimal getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(BigDecimal attendanceRate) {
        this.attendanceRate = attendanceRate;
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
}
