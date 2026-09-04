package com.crimsonlogic.creditcardmanagementsystem.dto;

public class RewardRecommendationResponseDto {

    private String recommendationId;
    private String customerId;
    private String offerName;
    private String reason;

    public RewardRecommendationResponseDto() {
    }

    public RewardRecommendationResponseDto(String recommendationId,
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
