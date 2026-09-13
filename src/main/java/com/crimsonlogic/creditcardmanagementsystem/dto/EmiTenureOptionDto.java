package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.math.BigDecimal;

public class EmiTenureOptionDto {

    private Integer tenureMonths;
    private BigDecimal monthlyEmi;
    private BigDecimal totalInterest;
    private BigDecimal totalPayable;
    private BigDecimal processingFee;

    public EmiTenureOptionDto() {
    }

    public EmiTenureOptionDto(Integer tenureMonths,
                              BigDecimal monthlyEmi,
                              BigDecimal totalInterest,
                              BigDecimal totalPayable,
                              BigDecimal processingFee) {
        this.tenureMonths = tenureMonths;
        this.monthlyEmi = monthlyEmi;
        this.totalInterest = totalInterest;
        this.totalPayable = totalPayable;
        this.processingFee = processingFee;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getMonthlyEmi() {
        return monthlyEmi;
    }

    public void setMonthlyEmi(BigDecimal monthlyEmi) {
        this.monthlyEmi = monthlyEmi;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(BigDecimal totalPayable) {
        this.totalPayable = totalPayable;
    }

    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }
}
