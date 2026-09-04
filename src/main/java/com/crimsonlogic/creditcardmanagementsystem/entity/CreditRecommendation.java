package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "credit_recommendations")
public class CreditRecommendation {

    @Id
    private String recommendationId;

    @Column(nullable = false)
    private String customerId;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentLimit;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal recommendedMin;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal recommendedMax;

    @Column(length = 1000)
    private String factors;

    public CreditRecommendation() {
    }

    public CreditRecommendation(String recommendationId,
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
