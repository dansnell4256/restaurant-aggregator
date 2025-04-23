package com.catalogue.model;


import com.common.model.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category extends TenantEntity {

    private String name;

    private String description;

    private Integer displayOrder;

    @Column(columnDefinition = "boolean default true")
    private Boolean active;

    @OneToMany(mappedBy = "category")
    private Set<CategoryItem> categoryItems = new HashSet<>();

//     Getters and setters
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<CategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(Set<CategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }
}
