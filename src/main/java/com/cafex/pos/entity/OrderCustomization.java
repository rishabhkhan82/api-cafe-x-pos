package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_customizations")
public class OrderCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "customization_id", nullable = false)
    private String customizationId;

    @Column(name = "customization_name", nullable = false)
    private String customizationName;

    @Column(name = "option_id", nullable = false)
    private String optionId;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "additional_price", nullable = false)
    private BigDecimal additionalPrice;

    // Constructors
    public OrderCustomization() {}

    public OrderCustomization(OrderItem orderItem, String customizationId, String customizationName,
                            String optionId, String optionName, BigDecimal additionalPrice) {
        this.orderItem = orderItem;
        this.customizationId = customizationId;
        this.customizationName = customizationName;
        this.optionId = optionId;
        this.optionName = optionName;
        this.additionalPrice = additionalPrice;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public String getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }

    public String getCustomizationName() {
        return customizationName;
    }

    public void setCustomizationName(String customizationName) {
        this.customizationName = customizationName;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public BigDecimal getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(BigDecimal additionalPrice) {
        this.additionalPrice = additionalPrice;
    }
}
