package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CardBlockRequestDto {

    @NotNull(message = "Target status is required")
    private CardStatus targetStatus;

    @NotBlank(message = "Reason is required")
    private String reason;

    public CardBlockRequestDto() {
    }

    public CardBlockRequestDto(CardStatus targetStatus, String reason) {
        this.targetStatus = targetStatus;
        this.reason = reason;
    }

    public CardStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(CardStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
