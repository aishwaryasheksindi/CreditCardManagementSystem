package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "statement_items")
public class StatementItem {

    @Id
    private String statementItemId;

    private String statementId;

    private String transactionId;

    private LocalDate itemDate;

    private String description;

    private BigDecimal amount;

    private String itemType;

    public StatementItem() {
    }

    public StatementItem(String statementItemId,
                         String statementId,
                         String transactionId,
                         LocalDate itemDate,
                         String description,
                         BigDecimal amount,
                         String itemType) {
        this.statementItemId = statementItemId;
        this.statementId = statementId;
        this.transactionId = transactionId;
        this.itemDate = itemDate;
        this.description = description;
        this.amount = amount;
        this.itemType = itemType;
    }

    public String getStatementItemId() {
        return statementItemId;
    }

    public void setStatementItemId(String statementItemId) {
        this.statementItemId = statementItemId;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getItemDate() {
        return itemDate;
    }

    public void setItemDate(LocalDate itemDate) {
        this.itemDate = itemDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}

