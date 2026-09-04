package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RewardRequestDto {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Earned points is required")
    @Min(value = 0, message = "Earned points cannot be negative")
    private Integer earnedPoints;

    @NotNull(message = "Redeemed points is required")
    @Min(value = 0, message = "Redeemed points cannot be negative")
    private Integer redeemedPoints;

    @NotNull(message = "Expired points is required")
    @Min(value = 0, message = "Expired points cannot be negative")
    private Integer expiredPoints;

    @NotNull(message = "Bonus points is required")
    @Min(value = 0, message = "Bonus points cannot be negative")
    private Integer bonusPoints;

    @NotNull(message = "Balance points is required")
    @Min(value = 0, message = "Balance points cannot be negative")
    private Integer balancePoints;

    public RewardRequestDto() {
    }

    public RewardRequestDto(String customerId,
                            Integer earnedPoints,
                            Integer redeemedPoints,
                            Integer expiredPoints,
                            Integer bonusPoints,
                            Integer balancePoints) {
        this.customerId = customerId;
        this.earnedPoints = earnedPoints;
        this.redeemedPoints = redeemedPoints;
        this.expiredPoints = expiredPoints;
        this.bonusPoints = bonusPoints;
        this.balancePoints = balancePoints;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(Integer earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public Integer getRedeemedPoints() {
        return redeemedPoints;
    }

    public void setRedeemedPoints(Integer redeemedPoints) {
        this.redeemedPoints = redeemedPoints;
    }

    public Integer getExpiredPoints() {
        return expiredPoints;
    }

    public void setExpiredPoints(Integer expiredPoints) {
        this.expiredPoints = expiredPoints;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public Integer getBalancePoints() {
        return balancePoints;
    }

    public void setBalancePoints(Integer balancePoints) {
        this.balancePoints = balancePoints;
    }
}
