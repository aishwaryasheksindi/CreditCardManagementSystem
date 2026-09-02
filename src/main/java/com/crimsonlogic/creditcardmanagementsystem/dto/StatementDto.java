package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StatementDto {

    private String statementId;

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @NotNull(message = "Statement date is required")
    private LocalDate statementDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Opening balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Opening balance cannot be negative")
    private BigDecimal openingBalance;

    @NotNull(message = "Total purchases is required")
    @PositiveOrZero(message = "Total purchases cannot be negative")
    private BigDecimal totalPurchases;

    @NotNull(message = "Total payments is required")
    @PositiveOrZero(message = "Total payments cannot be negative")
    private BigDecimal totalPayments;

    @NotNull(message = "Total refunds is required")
    @PositiveOrZero(message = "Total refunds cannot be negative")
    private BigDecimal totalRefunds;

    @NotNull(message = "Total fees is required")
    @PositiveOrZero(message = "Total fees cannot be negative")
    private BigDecimal totalFees;

    @NotNull(message = "Total interest is required")
    @PositiveOrZero(message = "Total interest cannot be negative")
    private BigDecimal totalInterest;

    private BigDecimal closingBalance;

    private BigDecimal minimumDue;

    public StatementDto() {
    }

    public StatementDto(String statementId,
                        String cardId,
                        LocalDate statementDate,
                        LocalDate dueDate,
                        BigDecimal openingBalance,
                        BigDecimal totalPurchases,
                        BigDecimal totalPayments,
                        BigDecimal totalRefunds,
                        BigDecimal totalFees,
                        BigDecimal totalInterest,
                        BigDecimal closingBalance,
                        BigDecimal minimumDue) {
        this.statementId = statementId;
        this.cardId = cardId;
        this.statementDate = statementDate;
        this.dueDate = dueDate;
        this.openingBalance = openingBalance;
        this.totalPurchases = totalPurchases;
        this.totalPayments = totalPayments;
        this.totalRefunds = totalRefunds;
        this.totalFees = totalFees;
        this.totalInterest = totalInterest;
        this.closingBalance = closingBalance;
        this.minimumDue = minimumDue;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public LocalDate getStatementDate() {
        return statementDate;
    }

    public void setStatementDate(LocalDate statementDate) {
        this.statementDate = statementDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(BigDecimal totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }

    public BigDecimal getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(BigDecimal totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public BigDecimal getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(BigDecimal totalFees) {
        this.totalFees = totalFees;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public BigDecimal getMinimumDue() {
        return minimumDue;
    }

    public void setMinimumDue(BigDecimal minimumDue) {
        this.minimumDue = minimumDue;
    }
}
