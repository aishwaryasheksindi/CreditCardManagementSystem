package com.crimsonlogic.creditcardmanagementsystem.dto;

public class AiFraudRiskResponseDto {

    private String transactionId;
    private Integer score;
    private String riskLevel;
    private String riskFactors;
    private String explanation;
    private String riskScoreId;
    private boolean fraudAlertCreated;

    public AiFraudRiskResponseDto() {
    }

    public AiFraudRiskResponseDto(String transactionId,
                                 Integer score,
                                 String riskLevel,
                                 String riskFactors,
                                 String explanation,
                                 String riskScoreId,
                                 boolean fraudAlertCreated) {
        this.transactionId = transactionId;
        this.score = score;
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors;
        this.explanation = explanation;
        this.riskScoreId = riskScoreId;
        this.fraudAlertCreated = fraudAlertCreated;
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

    public String getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getRiskScoreId() {
        return riskScoreId;
    }

    public void setRiskScoreId(String riskScoreId) {
        this.riskScoreId = riskScoreId;
    }

    public boolean isFraudAlertCreated() {
        return fraudAlertCreated;
    }

    public void setFraudAlertCreated(boolean fraudAlertCreated) {
        this.fraudAlertCreated = fraudAlertCreated;
    }
}
