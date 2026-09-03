package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardResponseDto {

    private String cardId;
    private String cardReference;
    private String customerId;
    private String cardTypeId;
    private CardStatus cardStatus;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private String billingCycle;
    private BigDecimal interestRate;
    private BigDecimal annualFee;
    private LocalDate expiryDate;
    private LocalDate issuanceDate;

    public CardResponseDto() {
    }

    public CardResponseDto(String cardId,
                           String cardReference,
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
        this.cardId = cardId;
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

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
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
