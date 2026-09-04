package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_plans")
public class EmiPlan {

    @Id
    private String emiPlanId;

    @Column(nullable = false)
    private String transactionId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal principal;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal emiAmount;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal processingFee;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal outstandingAmount;

    @Column(nullable = false)
    private String status;

    private LocalDate nextDueDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal lateFeeAmount = BigDecimal.ZERO;

    private Integer missedInstallments = 0;

    public EmiPlan() {
    }

    public EmiPlan(String emiPlanId,
                   String transactionId,
                   BigDecimal principal,
                   BigDecimal interestRate,
                   Integer tenureMonths,
                   BigDecimal emiAmount,
                   BigDecimal processingFee,
                   LocalDate startDate,
                   LocalDate endDate,
                   BigDecimal outstandingAmount,
                   String status) {
        this.emiPlanId = emiPlanId;
        this.transactionId = transactionId;
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.emiAmount = emiAmount;
        this.processingFee = processingFee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.outstandingAmount = outstandingAmount;
        this.status = status;
    }

    public String getEmiPlanId() {
        return emiPlanId;
    }

    public void setEmiPlanId(String emiPlanId) {
        this.emiPlanId = emiPlanId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(BigDecimal emiAmount) {
        this.emiAmount = emiAmount;
    }

    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public BigDecimal getLateFeeAmount() {
        return lateFeeAmount;
    }

    public void setLateFeeAmount(BigDecimal lateFeeAmount) {
        this.lateFeeAmount = lateFeeAmount;
    }

    public Integer getMissedInstallments() {
        return missedInstallments;
    }

    public void setMissedInstallments(Integer missedInstallments) {
        this.missedInstallments = missedInstallments;
    }
}
