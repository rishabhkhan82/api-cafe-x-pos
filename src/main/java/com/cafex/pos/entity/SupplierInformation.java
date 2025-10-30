package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "supplier_information")
public class SupplierInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false, unique = true)
    private String supplierId;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Embedded
    private Address address;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "delivery_time", nullable = false)
    private Integer deliveryTime = 1;

    @Column(name = "minimum_order_value")
    private BigDecimal minimumOrderValue;

    @ElementCollection
    @CollectionTable(name = "supplier_categories", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "category")
    private List<String> categories;

    @Column
    private BigDecimal rating;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "contract_start_date")
    private LocalDateTime contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDateTime contractEndDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SupplierInformation() {}

    public SupplierInformation(String supplierId, String name, String email, String phone) {
        this.supplierId = supplierId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isActive = true;
        this.deliveryTime = 1;
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

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public BigDecimal getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(BigDecimal minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(LocalDateTime contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public LocalDateTime getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(LocalDateTime contractEndDate) {
        this.contractEndDate = contractEndDate;
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

    // Helper methods
    public boolean isContractActive() {
        if (contractStartDate == null || contractEndDate == null) {
            return true; // No contract dates means always active
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(contractStartDate) && now.isBefore(contractEndDate);
    }

    public boolean isContractExpired() {
        return contractEndDate != null && LocalDateTime.now().isAfter(contractEndDate);
    }

    public boolean isContractUpcoming() {
        return contractStartDate != null && LocalDateTime.now().isBefore(contractStartDate);
    }

    public long getDaysUntilContractExpiry() {
        if (contractEndDate == null) return Long.MAX_VALUE;
        return java.time.Duration.between(LocalDateTime.now(), contractEndDate).toDays();
    }

    public boolean suppliesCategory(String category) {
        return categories != null && categories.contains(category);
    }

    public void addCategory(String category) {
        if (this.categories == null) {
            this.categories = new java.util.ArrayList<>();
        }
        if (!this.categories.contains(category)) {
            this.categories.add(category);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeCategory(String category) {
        if (this.categories != null) {
            this.categories.remove(category);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateRating(BigDecimal newRating) {
        if (this.rating == null) {
            this.rating = newRating;
        } else {
            // Simple average calculation - in real implementation, you'd track number of ratings
            this.rating = this.rating.add(newRating).divide(BigDecimal.valueOf(2));
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    // Embedded Address class
    @Embeddable
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String pincode;
        private String country;

        public Address() {}

        public Address(String street, String city, String state, String pincode, String country) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.pincode = pincode;
            this.country = country;
        }

        // Getters and Setters for Address
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getPincode() { return pincode; }
        public void setPincode(String pincode) { this.pincode = pincode; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
}
