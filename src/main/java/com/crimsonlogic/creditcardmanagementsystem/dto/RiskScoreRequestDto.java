package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RiskScoreRequestDto {

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Integer score;

    private String modelVersion;

    private String riskFactors;

    public RiskScoreRequestDto() {
    }

    public RiskScoreRequestDto(String transactionId,
                               Integer score,
                               String modelVersion,
                               String riskFactors) {
        this.transactionId = transactionId;
        this.score = score;
        this.modelVersion = modelVersion;
        this.riskFactors = riskFactors;
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

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }
}
