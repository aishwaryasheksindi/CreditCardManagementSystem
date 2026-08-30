package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_categories")
public class TransactionCategory {

    @Id
    private String categoryId;

    private String categoryName;

    private String description;


    // Default constructor
    public TransactionCategory() {
    }


    // Parameterized constructor
    public TransactionCategory(String categoryId,
                    String categoryName,
                    String description) {

        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }


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