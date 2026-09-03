package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

import com.crimsonlogic.creditcardmanagementsystem.enums.CustomerStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CustomerRequestDto {

    @NotBlank(message = "Name is required")
    @Pattern(
        regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
        message = "Name must contain only letters and spaces"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Phone number must contain 10 digits and start with 6, 7, 8 or 9"
    )
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    private LocalDate dateOfBirth;

    @NotBlank(message = "Employment is required")
    private String employment;

    @NotBlank(message = "Income range is required")
    private String incomeRange;

    private KycStatus kycStatus;

    private String creditProfile;

    private CustomerStatus customerStatus;

    public CustomerRequestDto() {
    }

    public CustomerRequestDto(String name, String email, String phoneNumber, String address, LocalDate dateOfBirth, String employment, String incomeRange, KycStatus kycStatus, String creditProfile, CustomerStatus customerStatus) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.employment = employment;
        this.incomeRange = incomeRange;
        this.kycStatus = kycStatus;
        this.creditProfile = creditProfile;
        this.customerStatus = customerStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getIncomeRange() {
        return incomeRange;
    }

    public void setIncomeRange(String incomeRange) {
        this.incomeRange = incomeRange;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getCreditProfile() {
        return creditProfile;
    }

    public void setCreditProfile(String creditProfile) {
        this.creditProfile = creditProfile;
    }

    public CustomerStatus getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(CustomerStatus customerStatus) {
        this.customerStatus = customerStatus;
    }
}
