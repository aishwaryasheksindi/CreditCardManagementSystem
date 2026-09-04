package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class EmiRecommendationRequestDto {

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Tenure months is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    private Integer tenureMonths;

    @NotNull(message = "Total payable is required")
    @DecimalMin(value = "0.0", message = "Total payable must be non-negative")
    private BigDecimal totalPayable;

    @NotNull(message = "Fees is required")
    @DecimalMin(value = "0.0", message = "Fees must be non-negative")
    private BigDecimal fees;

    public EmiRecommendationRequestDto() {
    }

    public EmiRecommendationRequestDto(String transactionId,
                                       Integer tenureMonths,
                                       BigDecimal totalPayable,
                                       BigDecimal fees) {
        this.transactionId = transactionId;
        this.tenureMonths = tenureMonths;
        this.totalPayable = totalPayable;
        this.fees = fees;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(BigDecimal totalPayable) {
        this.totalPayable = totalPayable;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }
}
