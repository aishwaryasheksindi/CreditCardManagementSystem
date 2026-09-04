package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRequestDto {

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @NotBlank(message = "Merchant ID is required")
    private String merchantId;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotNull(message = "Transaction amount is required")
    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "Currency must be a valid 3-letter uppercase code"
    )
    private String currency;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactionDate;

    @NotBlank(message = "Transaction location is required")
    @Size(max = 200, message = "Transaction location must not exceed 200 characters")
    private String transactionLocation;

    @NotNull(message = "Transaction status is required")
    private TransactionStatus transactionStatus;

    private TransactionType transactionType;

    private String pin; // required only for transaction types that need PIN verification, e.g. CASH_WITHDRAWAL — no @NotBlank, since not all transaction types need it

    public TransactionRequestDto() {
    }

    public TransactionRequestDto(String cardId,
                                 String merchantId,
                                 String categoryId,
                                 BigDecimal amount,
                                 String currency,
                                 LocalDateTime transactionDate,
                                 String transactionLocation,
                                 TransactionStatus transactionStatus) {
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.currency = currency;
        this.transactionDate = transactionDate;
        this.transactionLocation = transactionLocation;
        this.transactionStatus = transactionStatus;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionLocation() {
        return transactionLocation;
    }

    public void setTransactionLocation(String transactionLocation) {
        this.transactionLocation = transactionLocation;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
