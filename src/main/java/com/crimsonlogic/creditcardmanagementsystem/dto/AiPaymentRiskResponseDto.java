package com.crimsonlogic.creditcardmanagementsystem.dto;

public class AiPaymentRiskResponseDto {

    private String customerId;
    private String riskLevel;
    private Double utilizationPercent;
    private Integer minimumOnlyCycleCount;
    private Integer lateCycleCount;
    private String explanation;

    public AiPaymentRiskResponseDto() {
    }

    public AiPaymentRiskResponseDto(String customerId,
                                   String riskLevel,
                                   Double utilizationPercent,
                                   Integer minimumOnlyCycleCount,
                                   Integer lateCycleCount,
                                   String explanation) {
        this.customerId = customerId;
        this.riskLevel = riskLevel;
        this.utilizationPercent = utilizationPercent;
        this.minimumOnlyCycleCount = minimumOnlyCycleCount;
        this.lateCycleCount = lateCycleCount;
        this.explanation = explanation;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getUtilizationPercent() {
        return utilizationPercent;
    }

    public void setUtilizationPercent(Double utilizationPercent) {
        this.utilizationPercent = utilizationPercent;
    }

    public Integer getMinimumOnlyCycleCount() {
        return minimumOnlyCycleCount;
    }

    public void setMinimumOnlyCycleCount(Integer minimumOnlyCycleCount) {
        this.minimumOnlyCycleCount = minimumOnlyCycleCount;
    }

    public Integer getLateCycleCount() {
        return lateCycleCount;
    }

    public void setLateCycleCount(Integer lateCycleCount) {
        this.lateCycleCount = lateCycleCount;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
