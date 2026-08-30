package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CardStatusHistoryDto {

    private String cardStatusHistoryId;

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @NotNull(message = "Card status is required")
    private CardStatus status;

    @NotNull(message = "Changed date and time is required")
    private LocalDateTime changedAt;

    @NotBlank(message = "Changed by is required")
    @Size(max = 100, message = "Changed by must not exceed 100 characters")
    private String changedBy;


    // Default constructor
    public CardStatusHistoryDto() {
    }


    // Parameterized constructor
    public CardStatusHistoryDto(String cardStatusHistoryId,
                                String cardId,
                                CardStatus status,
                                LocalDateTime changedAt,
                                String changedBy) {

        this.cardStatusHistoryId = cardStatusHistoryId;
        this.cardId = cardId;
        this.status = status;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
    }


    // Getters and Setters

    public String getCardStatusHistoryId() {
        return cardStatusHistoryId;
    }

    public void setCardStatusHistoryId(String cardStatusHistoryId) {
        this.cardStatusHistoryId = cardStatusHistoryId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}