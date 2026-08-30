package com.crimsonlogic.creditcardmanagementsystem.entity;

import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private TransactionCategory category;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime transactionDate;

    private String transactionLocation;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;


    // Default constructor
    public Transaction() {
    }


    // Parameterized constructor
    public Transaction(String transactionId,
                       Card card,
                       Merchant merchant,
                       TransactionCategory category,
                       BigDecimal amount,
                       String currency,
                       LocalDateTime transactionDate,
                       String transactionLocation,
                       TransactionStatus transactionStatus) {

        this.transactionId = transactionId;
        this.card = card;
        this.merchant = merchant;
        this.category = category;
        this.amount = amount;
        this.currency = currency;
        this.transactionDate = transactionDate;
        this.transactionLocation = transactionLocation;
        this.transactionStatus = transactionStatus;
    }


    // Getters and Setters

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
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