package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MerchantRequestDto {

    @NotBlank(message = "Merchant name is required")
    @Pattern(
            regexp = "^[A-Za-z0-9 &.,'-]+$",
            message = "Merchant name contains invalid characters"
    )
    @Size(max = 100, message = "Merchant name must not exceed 100 characters")
    private String merchantName;

    @NotBlank(message = "Merchant category is required")
    @Size(max = 100, message = "Merchant category must not exceed 100 characters")
    private String merchantCategory;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @NotBlank(message = "Contact details are required")
    @Size(max = 100, message = "Contact details must not exceed 100 characters")
    private String contactDetails;

    public MerchantRequestDto() {
    }

    public MerchantRequestDto(String merchantName,
                             String merchantCategory,
                             String location,
                             String contactDetails) {
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.location = location;
        this.contactDetails = contactDetails;
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
