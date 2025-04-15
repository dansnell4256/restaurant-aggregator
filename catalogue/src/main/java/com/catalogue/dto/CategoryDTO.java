package com.catalogue.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean active;
    private List<CategoryItemSummaryDTO> items = new ArrayList<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<CategoryItemSummaryDTO> getItems() {
        return items;
    }

    public void setItems(List<CategoryItemSummaryDTO> items) {
        this.items = items;
    }
}
