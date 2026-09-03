package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class TransactionCategoryRequestDto {

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;

    public TransactionCategoryRequestDto() {
    }

    public TransactionCategoryRequestDto(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
