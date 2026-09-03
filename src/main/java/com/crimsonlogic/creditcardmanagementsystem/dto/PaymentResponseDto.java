package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDto {

    private String paymentId;
    private String cardId;
    private String customerId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentType;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String referenceNumber;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(String paymentId,
                              String cardId,
                              String customerId,
                              BigDecimal amount,
                              LocalDateTime paymentDate,
                              String paymentType,
                              PaymentStatus paymentStatus,
                              String paymentMethod,
                              String referenceNumber) {
        this.paymentId = paymentId;
        this.cardId = cardId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
