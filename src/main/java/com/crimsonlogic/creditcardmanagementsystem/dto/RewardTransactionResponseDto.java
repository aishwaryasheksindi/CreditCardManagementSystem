package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDateTime;

public class RewardTransactionResponseDto {

    private String rewardTransactionId;
    private String rewardId;
    private Integer points;
    private String transactionType;
    private String description;
    private LocalDateTime transactionDate;

    public RewardTransactionResponseDto() {
    }

    public RewardTransactionResponseDto(String rewardTransactionId,
                                        String rewardId,
                                        Integer points,
                                        String transactionType,
                                        String description,
                                        LocalDateTime transactionDate) {
        this.rewardTransactionId = rewardTransactionId;
        this.rewardId = rewardId;
        this.points = points;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public String getRewardTransactionId() {
        return rewardTransactionId;
    }

    public void setRewardTransactionId(String rewardTransactionId) {
        this.rewardTransactionId = rewardTransactionId;
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
