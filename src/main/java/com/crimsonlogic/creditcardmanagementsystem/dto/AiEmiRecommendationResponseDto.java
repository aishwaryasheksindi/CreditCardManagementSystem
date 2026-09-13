package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiEmiRecommendationResponseDto {

    private String transactionId;
    private BigDecimal transactionAmount;
    private String cardLast4;
    private boolean eligible;
    private String eligibilityReason;
    private Integer recommendedTenureMonths;
    private BigDecimal recommendedEmiAmount;
    private BigDecimal recommendedTotalInterest;
    private BigDecimal recommendedTotalPayable;
    private BigDecimal processingFee;
    private BigDecimal annualInterestRate;
    private String recommendationReason;
    private boolean aiGenerated;
    private List<EmiTenureOptionDto> availableOptions;

    public AiEmiRecommendationResponseDto() {
    }

    public AiEmiRecommendationResponseDto(String transactionId,
                                          BigDecimal transactionAmount,
                                          String cardLast4,
                                          boolean eligible,
                                          String eligibilityReason,
                                          Integer recommendedTenureMonths,
                                          BigDecimal recommendedEmiAmount,
                                          BigDecimal recommendedTotalInterest,
                                          BigDecimal recommendedTotalPayable,
                                          BigDecimal processingFee,
                                          BigDecimal annualInterestRate,
                                          String recommendationReason,
                                          boolean aiGenerated,
                                          List<EmiTenureOptionDto> availableOptions) {
        this.transactionId = transactionId;
        this.transactionAmount = transactionAmount;
        this.cardLast4 = cardLast4;
        this.eligible = eligible;
        this.eligibilityReason = eligibilityReason;
        this.recommendedTenureMonths = recommendedTenureMonths;
        this.recommendedEmiAmount = recommendedEmiAmount;
        this.recommendedTotalInterest = recommendedTotalInterest;
        this.recommendedTotalPayable = recommendedTotalPayable;
        this.processingFee = processingFee;
        this.annualInterestRate = annualInterestRate;
        this.recommendationReason = recommendationReason;
        this.aiGenerated = aiGenerated;
        this.availableOptions = availableOptions;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getEligibilityReason() {
        return eligibilityReason;
    }

    public void setEligibilityReason(String eligibilityReason) {
        this.eligibilityReason = eligibilityReason;
    }

    public Integer getRecommendedTenureMonths() {
        return recommendedTenureMonths;
    }

    public void setRecommendedTenureMonths(Integer recommendedTenureMonths) {
        this.recommendedTenureMonths = recommendedTenureMonths;
    }

    public BigDecimal getRecommendedEmiAmount() {
        return recommendedEmiAmount;
    }

    public void setRecommendedEmiAmount(BigDecimal recommendedEmiAmount) {
        this.recommendedEmiAmount = recommendedEmiAmount;
    }

    public BigDecimal getRecommendedTotalInterest() {
        return recommendedTotalInterest;
    }

    public void setRecommendedTotalInterest(BigDecimal recommendedTotalInterest) {
        this.recommendedTotalInterest = recommendedTotalInterest;
    }

    public BigDecimal getRecommendedTotalPayable() {
        return recommendedTotalPayable;
    }

    public void setRecommendedTotalPayable(BigDecimal recommendedTotalPayable) {
        this.recommendedTotalPayable = recommendedTotalPayable;
    }

    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public boolean isAiGenerated() {
        return aiGenerated;
    }

    public void setAiGenerated(boolean aiGenerated) {
        this.aiGenerated = aiGenerated;
    }

    public List<EmiTenureOptionDto> getAvailableOptions() {
        return availableOptions;
    }

    public void setAvailableOptions(List<EmiTenureOptionDto> availableOptions) {
        this.availableOptions = availableOptions;
    }
}
