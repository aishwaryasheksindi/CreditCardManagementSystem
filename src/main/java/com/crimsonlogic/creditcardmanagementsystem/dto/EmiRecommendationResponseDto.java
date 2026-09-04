package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;

public class EmiRecommendationResponseDto {

    private String recommendationId;
    private String transactionId;
    private Integer tenureMonths;
    private BigDecimal totalPayable;
    private BigDecimal fees;

    public EmiRecommendationResponseDto() {
    }

    public EmiRecommendationResponseDto(String recommendationId,
                                        String transactionId,
                                        Integer tenureMonths,
                                        BigDecimal totalPayable,
                                        BigDecimal fees) {
        this.recommendationId = recommendationId;
        this.transactionId = transactionId;
        this.tenureMonths = tenureMonths;
        this.totalPayable = totalPayable;
        this.fees = fees;
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(BigDecimal totalPayable) {
        this.totalPayable = totalPayable;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }
}
