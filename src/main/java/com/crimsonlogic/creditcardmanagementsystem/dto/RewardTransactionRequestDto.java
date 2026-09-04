package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class RewardTransactionRequestDto {

    @NotBlank(message = "Reward ID is required")
    private String rewardId;

    @NotNull(message = "Points is required")
    @Min(value = 0, message = "Points cannot be negative")
    private Integer points;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "EARNED|REDEEMED|EXPIRED|BONUS", message = "Transaction type must be EARNED, REDEEMED, EXPIRED, or BONUS")
    private String transactionType;

    private String description;

    private LocalDateTime transactionDate;

    public RewardTransactionRequestDto() {
    }

    public RewardTransactionRequestDto(String rewardId,
                                       Integer points,
                                       String transactionType,
                                       String description,
                                       LocalDateTime transactionDate) {
        this.rewardId = rewardId;
        this.points = points;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
