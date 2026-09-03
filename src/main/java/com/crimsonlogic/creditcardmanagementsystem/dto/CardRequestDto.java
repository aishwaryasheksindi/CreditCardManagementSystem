package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardRequestDto {

    @NotBlank(message = "Card reference is required")
    @Size(max = 100, message = "Card reference must not exceed 100 characters")
    private String cardReference;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Card type ID is required")
    private String cardTypeId;

    @NotNull(message = "Card status is required")
    private CardStatus cardStatus;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "10000.0", message = "Credit limit must be at least ₹10,000")
    @DecimalMax(value = "2000000.0", message = "Credit limit cannot exceed ₹20,00,000")
    private BigDecimal creditLimit;

    @NotNull(message = "Available limit is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Available limit cannot be negative")
    private BigDecimal availableLimit;

    @NotBlank(message = "Billing cycle is required")
    @Size(max = 50, message = "Billing cycle must not exceed 50 characters")
    private String billingCycle;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "1.0", message = "Interest rate must be at least 1%")
    @DecimalMax(value = "42.0", message = "Interest rate cannot exceed 42% (typical credit card range)")
    private BigDecimal interestRate;

    @NotNull(message = "Annual fee is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Annual fee cannot be negative")
    private BigDecimal annualFee;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Issuance date is required")
    private LocalDate issuanceDate;

    public CardRequestDto() {
    }

    public CardRequestDto(String cardReference,
                          String customerId,
                          String cardTypeId,
                          CardStatus cardStatus,
                          BigDecimal creditLimit,
                          BigDecimal availableLimit,
                          String billingCycle,
                          BigDecimal interestRate,
                          BigDecimal annualFee,
                          LocalDate expiryDate,
                          LocalDate issuanceDate) {
        this.cardReference = cardReference;
        this.customerId = customerId;
        this.cardTypeId = cardTypeId;
        this.cardStatus = cardStatus;
        this.creditLimit = creditLimit;
        this.availableLimit = availableLimit;
        this.billingCycle = billingCycle;
        this.interestRate = interestRate;
        this.annualFee = annualFee;
        this.expiryDate = expiryDate;
        this.issuanceDate = issuanceDate;
    }

    public String getCardReference() {
        return cardReference;
    }

    public void setCardReference(String cardReference) {
        this.cardReference = cardReference;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(BigDecimal availableLimit) {
        this.availableLimit = availableLimit;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(LocalDate issuanceDate) {
        this.issuanceDate = issuanceDate;
    }
}
