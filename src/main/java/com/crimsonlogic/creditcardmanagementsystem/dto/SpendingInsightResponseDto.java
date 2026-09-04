package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SpendingInsightResponseDto {

    private String insightId;
    private String customerId;
    private String observation;
    private BigDecimal amount;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    public SpendingInsightResponseDto() {
    }

    public SpendingInsightResponseDto(String insightId,
                                      String customerId,
                                      String observation,
                                      BigDecimal amount,
                                      LocalDate periodStart,
                                      LocalDate periodEnd) {
        this.insightId = insightId;
        this.customerId = customerId;
        this.observation = observation;
        this.amount = amount;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    public String getInsightId() {
        return insightId;
    }

    public void setInsightId(String insightId) {
        this.insightId = insightId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
