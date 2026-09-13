package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.ChatRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final IAiAssistantService aiAssistantService;

    public AiAssistantController(IAiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDto> chat(@Valid @RequestBody ChatRequestDto requestDto) {
        ChatResponseDto response = aiAssistantService.processChat(requestDto);
        return ResponseEntity.ok(response);
    }
}
