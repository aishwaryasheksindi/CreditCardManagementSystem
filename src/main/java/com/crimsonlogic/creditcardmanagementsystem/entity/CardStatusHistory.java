package com.crimsonlogic.creditcardmanagementsystem.entity;

import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_status_history")
public class CardStatusHistory {

    @Id
    private String cardStatusHistoryId;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private LocalDateTime changedAt;

    private String changedBy;


    // Default constructor
    public CardStatusHistory() {
    }


    // Parameterized constructor
    public CardStatusHistory(String cardStatusHistoryId,
                             Card card,
                             CardStatus status,
                             LocalDateTime changedAt,
                             String changedBy) {

        this.cardStatusHistoryId = cardStatusHistoryId;
        this.card = card;
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

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
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