package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "referral_program")
public class ReferralProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referral_id", nullable = false, unique = true)
    private String referralId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id", nullable = false)
    private Customer referrer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referee_id", nullable = false)
    private Customer referee;

    @Column(name = "referral_code", nullable = false, unique = true)
    private String referralCode;

    @Column(name = "referral_status", nullable = false)
    private String referralStatus;

    @Column(name = "reward_type", nullable = false)
    private String rewardType;

    @Column(name = "reward_value")
    private BigDecimal rewardValue;

    @Column(name = "reward_status", nullable = false)
    private String rewardStatus;

    @Column(name = "referee_first_order_id")
    private String refereeFirstOrderId;

    @Column(name = "referee_signup_date")
    private LocalDateTime refereeSignupDate;

    @Column(name = "reward_credited_at")
    private LocalDateTime rewardCreditedAt;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "terms_accepted", nullable = false)
    private Boolean termsAccepted = false;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "device_info", columnDefinition = "JSON")
    private String deviceInfo;

    @Column(name = "campaign_id")
    private String campaignId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public ReferralProgram() {}

    public ReferralProgram(String referralId, Customer referrer, Customer referee, String referralCode, String referralStatus, String rewardType) {
        this.referralId = referralId;
        this.referrer = referrer;
        this.referee = referee;
        this.referralCode = referralCode;
        this.referralStatus = referralStatus;
        this.rewardType = rewardType;
        this.rewardStatus = "pending";
        this.termsAccepted = false;
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

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public Customer getReferrer() {
        return referrer;
    }

    public void setReferrer(Customer referrer) {
        this.referrer = referrer;
    }

    public Customer getReferee() {
        return referee;
    }

    public void setReferee(Customer referee) {
        this.referee = referee;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferralStatus() {
        return referralStatus;
    }

    public void setReferralStatus(String referralStatus) {
        this.referralStatus = referralStatus;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public BigDecimal getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(BigDecimal rewardValue) {
        this.rewardValue = rewardValue;
    }

    public String getRewardStatus() {
        return rewardStatus;
    }

    public void setRewardStatus(String rewardStatus) {
        this.rewardStatus = rewardStatus;
    }

    public String getRefereeFirstOrderId() {
        return refereeFirstOrderId;
    }

    public void setRefereeFirstOrderId(String refereeFirstOrderId) {
        this.refereeFirstOrderId = refereeFirstOrderId;
    }

    public LocalDateTime getRefereeSignupDate() {
        return refereeSignupDate;
    }

    public void setRefereeSignupDate(LocalDateTime refereeSignupDate) {
        this.refereeSignupDate = refereeSignupDate;
    }

    public LocalDateTime getRewardCreditedAt() {
        return rewardCreditedAt;
    }

    public void setRewardCreditedAt(LocalDateTime rewardCreditedAt) {
        this.rewardCreditedAt = rewardCreditedAt;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
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

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
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
