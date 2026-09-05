package com.crimsonlogic.creditcardmanagementsystem.entity;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    private String cardId;

    private String cardReference;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "card_type_id", nullable = false)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    private BigDecimal creditLimit;

    private BigDecimal availableLimit;

    private Integer billingCycle;

    private BigDecimal interestRate;

    private BigDecimal annualFee;

    private LocalDate expiryDate;

    private LocalDate issuanceDate;

    private String pinHash;

    private LocalDateTime pinSetAt;

    private int failedPinAttempts = 0;


    // Default constructor
    public Card() {
    }


    // Parameterized constructor
    public Card(String cardId,
                String cardReference,
                Customer customer,
                CardType cardType,
                CardStatus cardStatus,
                BigDecimal creditLimit,
                BigDecimal availableLimit,
                Integer billingCycle,
                BigDecimal interestRate,
                BigDecimal annualFee,
                LocalDate expiryDate,
                LocalDate issuanceDate) {

        this.cardId = cardId;
        this.cardReference = cardReference;
        this.customer = customer;
        this.cardType = cardType;
        this.cardStatus = cardStatus;
        this.creditLimit = creditLimit;
        this.availableLimit = availableLimit;
        this.billingCycle = billingCycle;
        this.interestRate = interestRate;
        this.annualFee = annualFee;
        this.expiryDate = expiryDate;
        this.issuanceDate = issuanceDate;
    }


    // Getters and Setters

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
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

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public LocalDateTime getPinSetAt() {
        return pinSetAt;
    }

    public void setPinSetAt(LocalDateTime pinSetAt) {
        this.pinSetAt = pinSetAt;
    }

    public int getFailedPinAttempts() {
        return failedPinAttempts;
    }

    public void setFailedPinAttempts(int failedPinAttempts) {
        this.failedPinAttempts = failedPinAttempts;
    }
}