package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StatementRequestDto {

    @NotBlank(message = "Card ID is required")
    private String cardId;

    private LocalDate statementDate;

    private LocalDate dueDate;

    private LocalDate cycleStartDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Opening balance cannot be negative")
    private BigDecimal openingBalance;

    public StatementRequestDto() {
    }

    public StatementRequestDto(String cardId, LocalDate statementDate, LocalDate dueDate) {
        this.cardId = cardId;
        this.statementDate = statementDate;
        this.dueDate = dueDate;
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

    public LocalDate getCycleStartDate() {
        return cycleStartDate;
    }

    public void setCycleStartDate(LocalDate cycleStartDate) {
        this.cycleStartDate = cycleStartDate;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }
}
