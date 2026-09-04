package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class RewardRecommendationRequestDto {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Offer name is required")
    private String offerName;

    @NotBlank(message = "Reason is required")
    private String reason;

    public RewardRecommendationRequestDto() {
    }

    public RewardRecommendationRequestDto(String customerId,
                                          String offerName,
                                          String reason) {
        this.customerId = customerId;
        this.offerName = offerName;
        this.reason = reason;
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
