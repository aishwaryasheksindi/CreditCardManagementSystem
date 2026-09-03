package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDto {

    private String transactionId;
    private String cardId;
    private String merchantId;
    private String categoryId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime transactionDate;
    private String transactionLocation;
    private TransactionStatus transactionStatus;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(String transactionId,
                                  String cardId,
                                  String merchantId,
                                  String categoryId,
                                  BigDecimal amount,
                                  String currency,
                                  LocalDateTime transactionDate,
                                  String transactionLocation,
                                  TransactionStatus transactionStatus) {
        this.transactionId = transactionId;
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.currency = currency;
        this.transactionDate = transactionDate;
        this.transactionLocation = transactionLocation;
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
}
