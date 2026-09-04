package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    private String chatId;

    @Column(nullable = false)
    private String customerId;

    @Column(length = 2000, nullable = false)
    private String question;

    @Column(length = 4000, nullable = false)
    private String answer;

    @Column(nullable = false)
    private LocalDateTime askedAt;

    public ChatHistory() {
    }

    public ChatHistory(String chatId,
                       String customerId,
                       String question,
                       String answer,
                       LocalDateTime askedAt) {
        this.chatId = chatId;
        this.customerId = customerId;
        this.question = question;
        this.answer = answer;
        this.askedAt = askedAt;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getAskedAt() {
        return askedAt;
    }

    public void setAskedAt(LocalDateTime askedAt) {
        this.askedAt = askedAt;
    }
}
