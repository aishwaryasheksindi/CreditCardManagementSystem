package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_scores")
public class RiskScore {

    @Id
    private String riskScoreId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String riskLevel;

    private String modelVersion;

    @Column(nullable = false)
    private LocalDateTime scoredAt;

    @Column(length = 1000)
    private String riskFactors;

    public RiskScore() {
    }

    public RiskScore(String riskScoreId,
                     String transactionId,
                     Integer score,
                     String riskLevel,
                     String modelVersion,
                     LocalDateTime scoredAt,
                     String riskFactors) {
        this.riskScoreId = riskScoreId;
        this.transactionId = transactionId;
        this.score = score;
        this.riskLevel = riskLevel;
        this.modelVersion = modelVersion;
        this.scoredAt = scoredAt;
        this.riskFactors = riskFactors;
    }

    public String getRiskScoreId() {
        return riskScoreId;
    }

    public void setRiskScoreId(String riskScoreId) {
        this.riskScoreId = riskScoreId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public LocalDateTime getScoredAt() {
        return scoredAt;
    }

    public void setScoredAt(LocalDateTime scoredAt) {
        this.scoredAt = scoredAt;
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }
}
