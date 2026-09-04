package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IChatHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-history")
public class ChatHistoryController {

    private final IChatHistoryService chatHistoryService;

    public ChatHistoryController(IChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping
    public ResponseEntity<ChatHistoryResponseDto> createChat(@Valid @RequestBody ChatHistoryRequestDto requestDto) {
        ChatHistoryResponseDto created = chatHistoryService.createChat(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatHistoryResponseDto> getChatById(@PathVariable String chatId) {
        return ResponseEntity.ok(chatHistoryService.getChatById(chatId));
    }

    @GetMapping
    public ResponseEntity<List<ChatHistoryResponseDto>> getAllChats(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(chatHistoryService.getChatsByCustomerId(customerId));
        }
        return ResponseEntity.ok(chatHistoryService.getAllChats());
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<ChatHistoryResponseDto> updateChat(@PathVariable String chatId,
                                                            @Valid @RequestBody ChatHistoryRequestDto requestDto) {
        return ResponseEntity.ok(chatHistoryService.updateChat(chatId, requestDto));
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable String chatId) {
        chatHistoryService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }
}
