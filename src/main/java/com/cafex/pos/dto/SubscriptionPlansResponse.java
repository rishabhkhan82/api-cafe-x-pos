package com.cafex.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SubscriptionPlansResponse {

    private Long id;

    private String plan_id;

    private String name;

    private String display_name;

    private String description;

    private BigDecimal price;

    private String currency;

    private String billing_cycle;

    private Integer max_restaurants;

    private Integer max_users;

    private List<String> features;

    private Boolean is_active;

    private Boolean is_popular;

    private Integer subscriber_count;

    private BigDecimal revenue;

    private Integer trial_days;

    private BigDecimal setup_fee;

    private Long created_by;

    private Long updated_by;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBilling_cycle() {
        return billing_cycle;
    }

    public void setBilling_cycle(String billing_cycle) {
        this.billing_cycle = billing_cycle;
    }

    public Integer getMax_restaurants() {
        return max_restaurants;
    }

    public void setMax_restaurants(Integer max_restaurants) {
        this.max_restaurants = max_restaurants;
    }

    public Integer getMax_users() {
        return max_users;
    }

    public void setMax_users(Integer max_users) {
        this.max_users = max_users;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public Boolean getIs_popular() {
        return is_popular;
    }

    public void setIs_popular(Boolean is_popular) {
        this.is_popular = is_popular;
    }

    public Integer getSubscriber_count() {
        return subscriber_count;
    }

    public void setSubscriber_count(Integer subscriber_count) {
        this.subscriber_count = subscriber_count;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Integer getTrial_days() {
        return trial_days;
    }

    public void setTrial_days(Integer trial_days) {
        this.trial_days = trial_days;
    }

    public BigDecimal getSetup_fee() {
        return setup_fee;
    }

    public void setSetup_fee(BigDecimal setup_fee) {
        this.setup_fee = setup_fee;
    }

    public Long getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Long created_by) {
        this.created_by = created_by;
    }

    public Long getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(Long updated_by) {
        this.updated_by = updated_by;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}