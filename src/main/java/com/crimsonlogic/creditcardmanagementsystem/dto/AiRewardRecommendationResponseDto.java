package com.crimsonlogic.creditcardmanagementsystem.dto;

public class AiRewardRecommendationResponseDto {

    private String customerId;
    private boolean hasRecommendation;
    private String offerName;
    private String reason;
    private String matchedCardId;

    public AiRewardRecommendationResponseDto() {
    }

    public AiRewardRecommendationResponseDto(String customerId,
                                            boolean hasRecommendation,
                                            String offerName,
                                            String reason,
                                            String matchedCardId) {
        this.customerId = customerId;
        this.hasRecommendation = hasRecommendation;
        this.offerName = offerName;
        this.reason = reason;
        this.matchedCardId = matchedCardId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isHasRecommendation() {
        return hasRecommendation;
    }

    public void setHasRecommendation(boolean hasRecommendation) {
        this.hasRecommendation = hasRecommendation;
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

    public String getMatchedCardId() {
        return matchedCardId;
    }

    public void setMatchedCardId(String matchedCardId) {
        this.matchedCardId = matchedCardId;
    }
}
