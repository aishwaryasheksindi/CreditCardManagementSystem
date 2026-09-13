package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.ChatRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatResponseDto;

public interface IAiAssistantService {
    ChatResponseDto processChat(ChatRequestDto requestDto);
}
