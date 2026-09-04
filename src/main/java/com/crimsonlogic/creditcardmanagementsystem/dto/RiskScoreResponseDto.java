package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDateTime;

public class RiskScoreResponseDto {

    private String riskScoreId;
    private String transactionId;
    private Integer score;
    private String riskLevel;
    private String modelVersion;
    private LocalDateTime scoredAt;
    private String riskFactors;

    public RiskScoreResponseDto() {
    }

    public RiskScoreResponseDto(String riskScoreId,
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
