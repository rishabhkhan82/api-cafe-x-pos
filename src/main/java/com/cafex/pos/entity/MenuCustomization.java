package com.cafex.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "menu_customizations")
public class MenuCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customization_id", nullable = false, unique = true)
    private String customizationId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CustomizationType type;

    @Column(name = "required", nullable = false)
    private Boolean required = false;

    // Note: Options relationship removed as MenuCustomizationOption entity was deleted
    // This will need to be restructured based on the schema requirements

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    // Constructors
    public MenuCustomization() {}

    public MenuCustomization(String customizationId, String name, CustomizationType type,
                           Boolean required, MenuItem menuItem) {
        this.customizationId = customizationId;
        this.name = name;
        this.type = type;
        this.required = required != null ? required : false;
        this.menuItem = menuItem;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomizationType getType() {
        return type;
    }

    public void setType(CustomizationType type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    // Options getter/setter removed as MenuCustomizationOption entity was deleted

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    // Enum
    public enum CustomizationType {
        SINGLE, MULTIPLE
    }
}
