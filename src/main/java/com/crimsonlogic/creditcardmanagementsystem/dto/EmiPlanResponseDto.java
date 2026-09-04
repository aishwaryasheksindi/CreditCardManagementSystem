package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmiPlanResponseDto {

    private String emiPlanId;
    private String transactionId;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal processingFee;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal outstandingAmount;
    private String status;

    public EmiPlanResponseDto() {
    }

    public EmiPlanResponseDto(String emiPlanId,
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
}
