package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardRequestDto {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Card type ID is required")
    private String cardTypeId;

    @NotNull(message = "Card status is required")
    private CardStatus cardStatus;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "10000.0", message = "Credit limit must be at least ₹10,000")
    @DecimalMax(value = "2000000.0", message = "Credit limit cannot exceed ₹20,000,00")
    private BigDecimal creditLimit;

    @NotNull(message = "Available limit is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Available limit cannot be negative")
    private BigDecimal availableLimit;

    @NotNull(message = "Billing cycle is required")
    @Min(value = 1, message = "Billing cycle day must be between 1 and 28")
    @Max(value = 28, message = "Billing cycle day must be between 1 and 28")
    private Integer billingCycle;

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

    public CardRequestDto(String customerId,
                          String cardTypeId,
                          CardStatus cardStatus,
                          BigDecimal creditLimit,
                          BigDecimal availableLimit,
                          Integer billingCycle,
                          BigDecimal interestRate,
                          BigDecimal annualFee,
                          LocalDate expiryDate,
                          LocalDate issuanceDate) {
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

    public Integer getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(Integer billingCycle) {
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
