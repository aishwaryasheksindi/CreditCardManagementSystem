package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRequestDto {

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    @NotBlank(message = "Payment type is required")
    @Pattern(
            regexp = "(?i)^(minimum|full|partial|custom)$",
            message = "Payment type must be one of: minimum, full, partial, custom"
    )
    private String paymentType;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;

    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    private String referenceNumber;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(String cardId,
                             String customerId,
                             BigDecimal amount,
                             LocalDateTime paymentDate,
                             String paymentType,
                             PaymentStatus paymentStatus,
                             String paymentMethod,
                             String referenceNumber) {
        this.cardId = cardId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
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
