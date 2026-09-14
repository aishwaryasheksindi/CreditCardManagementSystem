package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDateTime;

public class CardActivationOtpResponseDto {

    private String cardId;
    private String otp;
    private LocalDateTime expiresAt;
    private String message;

    public CardActivationOtpResponseDto() {
    }

    public CardActivationOtpResponseDto(String cardId, String otp, LocalDateTime expiresAt, String message) {
        this.cardId = cardId;
        this.otp = otp;
        this.expiresAt = expiresAt;
        this.message = message;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
