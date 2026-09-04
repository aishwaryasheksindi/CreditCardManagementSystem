package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmiPlanRequestDto {

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Principal must be greater than 0")
    private BigDecimal principal;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", message = "Interest rate must be non-negative")
    private BigDecimal interestRate;

    @NotNull(message = "Tenure months is required")
    @Min(value = 3, message = "Tenure must be at least 3 months")
    @Max(value = 24, message = "Tenure cannot exceed 24 months")
    private Integer tenureMonths;

    @NotNull(message = "EMI amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "EMI amount must be greater than 0")
    private BigDecimal emiAmount;

    @NotNull(message = "Processing fee is required")
    @DecimalMin(value = "0.0", message = "Processing fee must be non-negative")
    private BigDecimal processingFee;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Outstanding amount is required")
    @DecimalMin(value = "0.0", message = "Outstanding amount must be non-negative")
    private BigDecimal outstandingAmount;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|COMPLETED|CANCELLED", message = "Status must be ACTIVE, COMPLETED, or CANCELLED")
    private String status;

    private LocalDate nextDueDate;

    public EmiPlanRequestDto() {
    }

    public EmiPlanRequestDto(String transactionId,
                             BigDecimal principal,
                             BigDecimal interestRate,
                             Integer tenureMonths,
                             BigDecimal emiAmount,
                             BigDecimal processingFee,
                             LocalDate startDate,
                             LocalDate endDate,
                             BigDecimal outstandingAmount,
                             String status) {
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
}
