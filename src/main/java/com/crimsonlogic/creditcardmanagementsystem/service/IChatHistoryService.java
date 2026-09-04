package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryResponseDto;

import java.util.List;

public interface IChatHistoryService {

    ChatHistoryResponseDto createChat(ChatHistoryRequestDto requestDto);

    ChatHistoryResponseDto getChatById(String chatId);

    List<ChatHistoryResponseDto> getAllChats();

    List<ChatHistoryResponseDto> getChatsByCustomerId(String customerId);

    ChatHistoryResponseDto updateChat(String chatId, ChatHistoryRequestDto requestDto);

    void deleteChat(String chatId);
}
