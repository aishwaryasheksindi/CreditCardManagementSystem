package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class CardActivationRequestDto {

    @NotBlank(message = "Activation OTP is required")
    private String otp;

    public CardActivationRequestDto() {
    }

    public CardActivationRequestDto(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
