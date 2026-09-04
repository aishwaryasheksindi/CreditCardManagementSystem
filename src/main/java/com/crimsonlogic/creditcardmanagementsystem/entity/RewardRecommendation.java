package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reward_recommendations")
public class RewardRecommendation {

    @Id
    private String recommendationId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String offerName;

    @Column(length = 1000, nullable = false)
    private String reason;

    public RewardRecommendation() {
    }

    public RewardRecommendation(String recommendationId,
                                String customerId,
                                String offerName,
                                String reason) {
        this.recommendationId = recommendationId;
        this.customerId = customerId;
        this.offerName = offerName;
        this.reason = reason;
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
