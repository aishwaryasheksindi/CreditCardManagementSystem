package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CardTypeRequestDto {

    @NotBlank(message = "Card type name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Card type name must contain only letters and spaces"
    )
    @Size(max = 50, message = "Card type name must not exceed 50 characters")
    private String typeName;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "10000.0", message = "Credit limit must be at least ₹10,000")
    @DecimalMax(value = "2000000.0", message = "Credit limit cannot exceed ₹20,00,000")
    private BigDecimal creditLimit;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "1.0", message = "Interest rate must be at least 1%")
    @DecimalMax(value = "42.0", message = "Interest rate cannot exceed 42% (typical credit card range)")
    private BigDecimal interestRate;

    @NotNull(message = "Reward rate is required")
    @PositiveOrZero(message = "Reward rate cannot be negative")
    private BigDecimal rewardRate;

    @NotNull(message = "Joining fee is required")
    @PositiveOrZero(message = "Joining fee cannot be negative")
    private BigDecimal joiningFee;

    @NotNull(message = "Annual fee is required")
    @PositiveOrZero(message = "Annual fee cannot be negative")
    private BigDecimal annualFee;

    @NotNull(message = "Cash withdrawal permission is required")
    private Boolean cashWithdrawalAllowed;

    @NotNull(message = "EMI eligibility is required")
    private Boolean emiEligible;

    @Size(max = 255, message = "Reward categories must not exceed 255 characters")
    private String rewardCategories;

    public CardTypeRequestDto() {
    }

    public CardTypeRequestDto(String typeName,
                             BigDecimal creditLimit,
                             BigDecimal interestRate,
                             BigDecimal rewardRate,
                             BigDecimal joiningFee,
                             BigDecimal annualFee,
                             Boolean cashWithdrawalAllowed,
                             Boolean emiEligible,
                             String rewardCategories) {
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
