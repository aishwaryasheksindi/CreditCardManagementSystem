package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "card_types")
public class CardType {

    @Id
    private String cardTypeId;

    private String typeName;

    private BigDecimal creditLimit;

    private BigDecimal interestRate;

    private BigDecimal rewardRate;

    private BigDecimal joiningFee;

    private BigDecimal annualFee;

    private Boolean cashWithdrawalAllowed;

    private Boolean emiEligible;

    private String rewardCategories;


    // Default constructor
    public CardType() {
    }


    // Parameterized constructor
    public CardType(String cardTypeId,
                    String typeName,
                    BigDecimal creditLimit,
                    BigDecimal interestRate,
                    BigDecimal rewardRate,
                    BigDecimal joiningFee,
                    BigDecimal annualFee,
                    Boolean cashWithdrawalAllowed,
                    Boolean emiEligible,
                    String rewardCategories) {

        this.cardTypeId = cardTypeId;
        this.typeName = typeName;
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.rewardRate = rewardRate;
        this.joiningFee = joiningFee;
        this.annualFee = annualFee;
        this.cashWithdrawalAllowed = cashWithdrawalAllowed;
        this.emiEligible = emiEligible;
        this.rewardCategories = rewardCategories;
    }


    // Getters and Setters

    public String getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getRewardRate() {
        return rewardRate;
    }

    public void setRewardRate(BigDecimal rewardRate) {
        this.rewardRate = rewardRate;
    }

    public BigDecimal getJoiningFee() {
        return joiningFee;
    }

    public void setJoiningFee(BigDecimal joiningFee) {
        this.joiningFee = joiningFee;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public Boolean getCashWithdrawalAllowed() {
        return cashWithdrawalAllowed;
    }

    public void setCashWithdrawalAllowed(Boolean cashWithdrawalAllowed) {
        this.cashWithdrawalAllowed = cashWithdrawalAllowed;
    }

    public Boolean getEmiEligible() {
        return emiEligible;
    }

    public void setEmiEligible(Boolean emiEligible) {
        this.emiEligible = emiEligible;
    }

    public String getRewardCategories() {
        return rewardCategories;
    }

    public void setRewardCategories(String rewardCategories) {
        this.rewardCategories = rewardCategories;
    }
}