package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "spending_insights")
public class SpendingInsight {

    @Id
    private String insightId;

    @Column(nullable = false)
    private String customerId;

    @Column(length = 1000, nullable = false)
    private String observation;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    public SpendingInsight() {
    }

    public SpendingInsight(String insightId,
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
