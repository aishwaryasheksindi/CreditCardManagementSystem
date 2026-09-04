package com.crimsonlogic.creditcardmanagementsystem.entity;

import java.time.LocalDate;

import com.crimsonlogic.creditcardmanagementsystem.enums.CustomerStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class Customer {

    @Id
    private String customerId;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;

    private String employment;

    private String incomeRange;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private String creditProfile;

    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;

    private String userId;

    public Customer() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}