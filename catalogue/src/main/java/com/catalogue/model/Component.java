package com.catalogue.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import com.common.model.TenantEntity;

@Entity
@Table(name = "components")
public class Component extends TenantEntity {

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private BigDecimal cost;

    private Boolean isAllergenic;

    private String allergenInfo;

    @ManyToMany(mappedBy = "components")
    private Set<CategoryItem> categoryItems = new HashSet<>();

    @ManyToMany(mappedBy = "components")
    private Set<CategoryItemCustomization> customizations = new HashSet<>();

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Boolean getIsAllergenic() {
        return isAllergenic;
    }

    public void setIsAllergenic(Boolean isAllergenic) {
        this.isAllergenic = isAllergenic;
    }

    public String getAllergenInfo() {
        return allergenInfo;
    }

    public void setAllergenInfo(String allergenInfo) {
        this.allergenInfo = allergenInfo;
    }

    public Set<CategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(Set<CategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    public Set<CategoryItemCustomization> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(Set<CategoryItemCustomization> customizations) {
        this.customizations = customizations;
    }
}
