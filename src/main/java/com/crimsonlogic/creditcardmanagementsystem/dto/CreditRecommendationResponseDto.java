package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;

public class CreditRecommendationResponseDto {

    private String recommendationId;
    private String customerId;
    private BigDecimal currentLimit;
    private BigDecimal recommendedMin;
    private BigDecimal recommendedMax;
    private String factors;

    public CreditRecommendationResponseDto() {
    }

    public CreditRecommendationResponseDto(String recommendationId,
                                           String customerId,
                                           BigDecimal currentLimit,
                                           BigDecimal recommendedMin,
                                           BigDecimal recommendedMax,
                                           String factors) {
        this.recommendationId = recommendationId;
        this.customerId = customerId;
        this.currentLimit = currentLimit;
        this.recommendedMin = recommendedMin;
        this.recommendedMax = recommendedMax;
        this.factors = factors;
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

    public BigDecimal getCurrentLimit() {
        return currentLimit;
    }

    public void setCurrentLimit(BigDecimal currentLimit) {
        this.currentLimit = currentLimit;
    }

    public BigDecimal getRecommendedMin() {
        return recommendedMin;
    }

    public void setRecommendedMin(BigDecimal recommendedMin) {
        this.recommendedMin = recommendedMin;
    }

    public BigDecimal getRecommendedMax() {
        return recommendedMax;
    }

    public void setRecommendedMax(BigDecimal recommendedMax) {
        this.recommendedMax = recommendedMax;
    }

    public String getFactors() {
        return factors;
    }

    public void setFactors(String factors) {
        this.factors = factors;
    }
}
