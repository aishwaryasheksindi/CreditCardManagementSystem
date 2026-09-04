package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDateTime;

public class ChatHistoryResponseDto {

    private String chatId;
    private String customerId;
    private String question;
    private String answer;
    private LocalDateTime askedAt;

    public ChatHistoryResponseDto() {
    }

    public ChatHistoryResponseDto(String chatId,
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
