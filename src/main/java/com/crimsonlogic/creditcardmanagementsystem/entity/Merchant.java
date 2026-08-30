package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    private String merchantId;

    private String merchantName;

    private String merchantCategory;

    private String location;

    private String contactDetails;


    // Default constructor
    public Merchant() {
    }


    // Parameterized constructor
    public Merchant(String merchantId,
                    String merchantName,
                    String merchantCategory,
                    String location,
                    String contactDetails) {

        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.location = location;
        this.contactDetails = contactDetails;
    }


    // Getters and Setters

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public void setMerchantCategory(String merchantCategory) {
        this.merchantCategory = merchantCategory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }
}