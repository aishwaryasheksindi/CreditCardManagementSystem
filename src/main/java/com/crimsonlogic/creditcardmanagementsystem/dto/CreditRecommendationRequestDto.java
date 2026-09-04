package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreditRecommendationRequestDto {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private BigDecimal currentLimit;

    @NotNull(message = "Recommended minimum is required")
    @DecimalMin(value = "0.0", message = "Recommended minimum must be non-negative")
    private BigDecimal recommendedMin;

    @NotNull(message = "Recommended maximum is required")
    @DecimalMin(value = "0.0", message = "Recommended maximum must be non-negative")
    private BigDecimal recommendedMax;

    private String factors;

    public CreditRecommendationRequestDto() {
    }

    public CreditRecommendationRequestDto(String customerId,
                                          BigDecimal currentLimit,
                                          BigDecimal recommendedMin,
                                          BigDecimal recommendedMax,
                                          String factors) {
        this.customerId = customerId;
        this.currentLimit = currentLimit;
        this.recommendedMin = recommendedMin;
        this.recommendedMax = recommendedMax;
        this.factors = factors;
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
