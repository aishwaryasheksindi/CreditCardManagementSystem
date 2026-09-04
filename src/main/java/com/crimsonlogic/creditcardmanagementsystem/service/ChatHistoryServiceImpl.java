package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.ChatHistory;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.ChatHistoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatHistoryServiceImpl implements IChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final CustomerRepository customerRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository,
                                  CustomerRepository customerRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.customerRepository = customerRepository;
    }

    private String generateUniqueChatId() {
        String id;
        do {
            id = IdGenerationUtil.generateChatId();
        } while (chatHistoryRepository.existsById(id));
        return id;
    }

    private void validateCustomerExists(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public ChatHistoryResponseDto createChat(ChatHistoryRequestDto requestDto) {
        validateCustomerExists(requestDto.getCustomerId());

        ChatHistory chat = new ChatHistory();
        chat.setChatId(generateUniqueChatId());
        chat.setCustomerId(requestDto.getCustomerId());
        chat.setQuestion(requestDto.getQuestion());
        chat.setAnswer(requestDto.getAnswer());
        chat.setAskedAt(
                requestDto.getAskedAt() != null ? requestDto.getAskedAt() : LocalDateTime.now()
        );

        ChatHistory saved = chatHistoryRepository.save(chat);
        return convertToResponseDto(saved);
    }

    @Override
    public ChatHistoryResponseDto getChatById(String chatId) {
        ChatHistory chat = chatHistoryRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat record not found with ID: " + chatId));
        return convertToResponseDto(chat);
    }

    @Override
    public List<ChatHistoryResponseDto> getAllChats() {
        return chatHistoryRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatHistoryResponseDto> getChatsByCustomerId(String customerId) {
        return chatHistoryRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChatHistoryResponseDto updateChat(String chatId, ChatHistoryRequestDto requestDto) {
        ChatHistory chat = chatHistoryRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat record not found with ID: " + chatId));

        if (!chat.getCustomerId().equals(requestDto.getCustomerId())) {
            validateCustomerExists(requestDto.getCustomerId());
            chat.setCustomerId(requestDto.getCustomerId());
        }

        chat.setQuestion(requestDto.getQuestion());
        chat.setAnswer(requestDto.getAnswer());
        if (requestDto.getAskedAt() != null) {
            chat.setAskedAt(requestDto.getAskedAt());
        }

        ChatHistory updated = chatHistoryRepository.save(chat);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteChat(String chatId) {
        ChatHistory chat = chatHistoryRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat record not found with ID: " + chatId));
        chatHistoryRepository.delete(chat);
    }

    private ChatHistoryResponseDto convertToResponseDto(ChatHistory chat) {
        return new ChatHistoryResponseDto(
                chat.getChatId(),
                chat.getCustomerId(),
                chat.getQuestion(),
                chat.getAnswer(),
                chat.getAskedAt()
        );
    }
}
