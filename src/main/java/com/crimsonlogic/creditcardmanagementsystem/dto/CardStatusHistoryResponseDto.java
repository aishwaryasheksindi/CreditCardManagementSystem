package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;

import java.time.LocalDateTime;

public class CardStatusHistoryResponseDto {

    private String cardStatusHistoryId;
    private String cardId;
    private CardStatus status;
    private LocalDateTime changedAt;
    private String changedBy;

    public CardStatusHistoryResponseDto() {
    }

    public CardStatusHistoryResponseDto(String cardStatusHistoryId,
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
