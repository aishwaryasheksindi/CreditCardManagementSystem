package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "emi_recommendations")
public class EmiRecommendation {

    @Id
    private String recommendationId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPayable;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal fees;

    public EmiRecommendation() {
    }

    public EmiRecommendation(String recommendationId,
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
