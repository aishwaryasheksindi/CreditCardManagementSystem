package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rewards")
public class Reward {

    @Id
    private String rewardId;

    @Column(nullable = false, unique = true)
    private String customerId;

    @Column(nullable = false)
    private Integer earnedPoints;

    @Column(nullable = false)
    private Integer redeemedPoints;

    @Column(nullable = false)
    private Integer expiredPoints;

    @Column(nullable = false)
    private Integer bonusPoints;

    @Column(nullable = false)
    private Integer balancePoints;

    public Reward() {
    }

    public Reward(String rewardId,
                  String customerId,
                  Integer earnedPoints,
                  Integer redeemedPoints,
                  Integer expiredPoints,
                  Integer bonusPoints,
                  Integer balancePoints) {
        this.rewardId = rewardId;
        this.customerId = customerId;
        this.earnedPoints = earnedPoints;
        this.redeemedPoints = redeemedPoints;
        this.expiredPoints = expiredPoints;
        this.bonusPoints = bonusPoints;
        this.balancePoints = balancePoints;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
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
