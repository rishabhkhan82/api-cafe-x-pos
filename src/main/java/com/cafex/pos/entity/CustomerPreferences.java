package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customer_preferences")
public class CustomerPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "notifications_enabled", nullable = false)
    private Boolean notificationsEnabled = true;

    @ElementCollection
    @CollectionTable(name = "customer_dietary_restrictions", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "restriction")
    private List<String> dietaryRestrictions;

    @ElementCollection
    @CollectionTable(name = "customer_favorite_categories", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "category")
    private List<String> favoriteCategories;

    @Column(name = "preferred_language")
    private String preferredLanguage = "en";

    @Column(name = "preferred_currency")
    private String preferredCurrency = "INR";

    @Column(name = "theme_preference")
    private String themePreference = "light";

    // Constructors
    public CustomerPreferences() {}

    public CustomerPreferences(Customer customer) {
        this.customer = customer;
        this.notificationsEnabled = true;
        this.preferredLanguage = "en";
        this.preferredCurrency = "INR";
        this.themePreference = "light";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public List<String> getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(List<String> dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public List<String> getFavoriteCategories() {
        return favoriteCategories;
    }

    public void setFavoriteCategories(List<String> favoriteCategories) {
        this.favoriteCategories = favoriteCategories;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public String getThemePreference() {
        return themePreference;
    }

    public void setThemePreference(String themePreference) {
        this.themePreference = themePreference;
    }
}
