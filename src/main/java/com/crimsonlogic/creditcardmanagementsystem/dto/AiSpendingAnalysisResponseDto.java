package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AiSpendingAnalysisResponseDto {

    private String customerId;
    private BigDecimal totalSpending;
    private Map<String, Double> categoryBreakdown;
    private List<String> topMerchants;
    private BigDecimal averageTransactionValue;
    private Double monthOverMonthChangePercent;
    private String summary;
    private String insightId;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    public AiSpendingAnalysisResponseDto() {
    }

    public AiSpendingAnalysisResponseDto(String customerId,
                                        BigDecimal totalSpending,
                                        Map<String, Double> categoryBreakdown,
                                        List<String> topMerchants,
                                        BigDecimal averageTransactionValue,
                                        Double monthOverMonthChangePercent,
                                        String summary,
                                        String insightId,
                                        LocalDate periodStart,
                                        LocalDate periodEnd) {
        this.customerId = customerId;
        this.totalSpending = totalSpending;
        this.categoryBreakdown = categoryBreakdown;
        this.topMerchants = topMerchants;
        this.averageTransactionValue = averageTransactionValue;
        this.monthOverMonthChangePercent = monthOverMonthChangePercent;
        this.summary = summary;
        this.insightId = insightId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(BigDecimal totalSpending) {
        this.totalSpending = totalSpending;
    }

    public Map<String, Double> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(Map<String, Double> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public List<String> getTopMerchants() {
        return topMerchants;
    }

    public void setTopMerchants(List<String> topMerchants) {
        this.topMerchants = topMerchants;
    }

    public BigDecimal getAverageTransactionValue() {
        return averageTransactionValue;
    }

    public void setAverageTransactionValue(BigDecimal averageTransactionValue) {
        this.averageTransactionValue = averageTransactionValue;
    }

    public Double getMonthOverMonthChangePercent() {
        return monthOverMonthChangePercent;
    }

    public void setMonthOverMonthChangePercent(Double monthOverMonthChangePercent) {
        this.monthOverMonthChangePercent = monthOverMonthChangePercent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getInsightId() {
        return insightId;
    }

    public void setInsightId(String insightId) {
        this.insightId = insightId;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
}
