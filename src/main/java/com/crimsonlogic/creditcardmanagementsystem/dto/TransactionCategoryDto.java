package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class TransactionCategoryDto {

    private String categoryId;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;


    // Default constructor
    public TransactionCategoryDto() {
    }


    // Parameterized constructor
    public TransactionCategoryDto(String categoryId,
                       String categoryName,
                       String description) {

        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }


    // Getters and Setters

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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