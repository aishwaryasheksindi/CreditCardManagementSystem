package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatResponseDto {

    private String chatId;
    private String message;
    private LocalDateTime timestamp;
    private List<String> dataSourcesUsed = new ArrayList<>();

    public ChatResponseDto() {
    }

    public ChatResponseDto(String chatId, String message, LocalDateTime timestamp, List<String> dataSourcesUsed) {
        this.chatId = chatId;
        this.message = message;
        this.timestamp = timestamp;
        this.dataSourcesUsed = dataSourcesUsed != null ? dataSourcesUsed : new ArrayList<>();
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDataSourcesUsed() {
        return dataSourcesUsed;
    }

    public void setDataSourcesUsed(List<String> dataSourcesUsed) {
        this.dataSourcesUsed = dataSourcesUsed != null ? dataSourcesUsed : new ArrayList<>();
    }
}
