package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "statements")
public class Statement {

    @Id
    private Long statementId;

    private Long cardId;

    private LocalDate statementDate;

    private LocalDate dueDate;

    private Double openingBalance;

    private Double totalPurchases;

    private Double totalPayments;

    private Double totalRefunds;

    private Double totalFees;

    private Double totalInterest;

    private Double closingBalance;

    private Double minimumDue;

    // Default constructor
    public Statement() {
    }

    // Parameterized constructor
    public Statement(Long statementId,
                     Long cardId,
                     LocalDate statementDate,
                     LocalDate dueDate,
                     Double openingBalance,
                     Double totalPurchases,
                     Double totalPayments,
                     Double totalRefunds,
                     Double totalFees,
                     Double totalInterest,
                     Double closingBalance,
                     Double minimumDue) {

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

    public Long getStatementId() {
        return statementId;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
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

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(Double totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public Double getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(Double totalPayments) {
        this.totalPayments = totalPayments;
    }

    public Double getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(Double totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public Double getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(Double totalFees) {
        this.totalFees = totalFees;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public Double getMinimumDue() {
        return minimumDue;
    }

    public void setMinimumDue(Double minimumDue) {
        this.minimumDue = minimumDue;
    }
}