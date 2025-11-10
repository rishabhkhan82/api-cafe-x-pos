package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "system_maintenance_logs")
public class SystemMaintenanceLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_id", nullable = false, unique = true)
    private String logId;

    @Column(name = "maintenance_type", nullable = false)
    private String maintenanceType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "planned_start_time")
    private LocalDateTime plannedStartTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "planned_end_time")
    private LocalDateTime plannedEndTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "impact_level", nullable = false)
    private String impactLevel;

    @Column(name = "affected_services", columnDefinition = "JSON")
    private String affectedServices;

    @Column(name = "affected_users")
    private Integer affectedUsers;

    @Column(name = "communication_sent", nullable = false)
    private Boolean communicationSent = false;

    @Column(name = "communication_method", columnDefinition = "JSON")
    private String communicationMethod;

    @Column(name = "rollback_plan", columnDefinition = "TEXT")
    private String rollbackPlan;

    @Column(name = "rollback_executed", nullable = false)
    private Boolean rollbackExecuted = false;

    @Column(name = "initiated_by")
    private String initiatedBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "team_members", columnDefinition = "JSON")
    private String teamMembers;

    @Column(name = "change_log", columnDefinition = "JSON")
    private String changeLog;

    @Column(name = "pre_checks", columnDefinition = "JSON")
    private String preChecks;

    @Column(name = "post_checks", columnDefinition = "JSON")
    private String postChecks;

    @Column(name = "issues_encountered", columnDefinition = "JSON")
    private String issuesEncountered;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(name = "downtime_minutes")
    private Integer downtimeMinutes;

    @Column(name = "user_impact_score")
    private Double userImpactScore;

    @Column(name = "success_rating")
    private Double successRating;

    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SystemMaintenanceLogs() {}

    public SystemMaintenanceLogs(String logId, String maintenanceType, String title, String description, String status, String impactLevel) {
        this.logId = logId;
        this.maintenanceType = maintenanceType;
        this.title = title;
        this.description = description;
        this.status = status;
        this.impactLevel = impactLevel;
        this.communicationSent = false;
        this.rollbackExecuted = false;
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

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImpactLevel() {
        return impactLevel;
    }

    public void setImpactLevel(String impactLevel) {
        this.impactLevel = impactLevel;
    }

    public String getAffectedServices() {
        return affectedServices;
    }

    public void setAffectedServices(String affectedServices) {
        this.affectedServices = affectedServices;
    }

    public Integer getAffectedUsers() {
        return affectedUsers;
    }

    public void setAffectedUsers(Integer affectedUsers) {
        this.affectedUsers = affectedUsers;
    }

    public Boolean getCommunicationSent() {
        return communicationSent;
    }

    public void setCommunicationSent(Boolean communicationSent) {
        this.communicationSent = communicationSent;
    }

    public String getCommunicationMethod() {
        return communicationMethod;
    }

    public void setCommunicationMethod(String communicationMethod) {
        this.communicationMethod = communicationMethod;
    }

    public String getRollbackPlan() {
        return rollbackPlan;
    }

    public void setRollbackPlan(String rollbackPlan) {
        this.rollbackPlan = rollbackPlan;
    }

    public Boolean getRollbackExecuted() {
        return rollbackExecuted;
    }

    public void setRollbackExecuted(Boolean rollbackExecuted) {
        this.rollbackExecuted = rollbackExecuted;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(String teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String getPreChecks() {
        return preChecks;
    }

    public void setPreChecks(String preChecks) {
        this.preChecks = preChecks;
    }

    public String getPostChecks() {
        return postChecks;
    }

    public void setPostChecks(String postChecks) {
        this.postChecks = postChecks;
    }

    public String getIssuesEncountered() {
        return issuesEncountered;
    }

    public void setIssuesEncountered(String issuesEncountered) {
        this.issuesEncountered = issuesEncountered;
    }

    public String getLessonsLearned() {
        return lessonsLearned;
    }

    public void setLessonsLearned(String lessonsLearned) {
        this.lessonsLearned = lessonsLearned;
    }

    public Integer getDowntimeMinutes() {
        return downtimeMinutes;
    }

    public void setDowntimeMinutes(Integer downtimeMinutes) {
        this.downtimeMinutes = downtimeMinutes;
    }

    public Double getUserImpactScore() {
        return userImpactScore;
    }

    public void setUserImpactScore(Double userImpactScore) {
        this.userImpactScore = userImpactScore;
    }

    public Double getSuccessRating() {
        return successRating;
    }

    public void setSuccessRating(Double successRating) {
        this.successRating = successRating;
    }

    public LocalDateTime getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDateTime nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
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
