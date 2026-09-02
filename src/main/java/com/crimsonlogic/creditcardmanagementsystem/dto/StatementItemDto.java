package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StatementItemDto {

    private String statementItemId;

    @NotBlank(message = "Statement ID is required")
    private String statementId;

    private String transactionId;

    @NotNull(message = "Item date is required")
    private LocalDate itemDate;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Item type is required")
    @Size(max = 50, message = "Item type must not exceed 50 characters")
    private String itemType;

    public StatementItemDto() {
    }

    public StatementItemDto(String statementItemId,
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
