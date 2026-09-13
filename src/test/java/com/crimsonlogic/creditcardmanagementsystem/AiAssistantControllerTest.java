package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AiAssistantController;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiAssistantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiAssistantControllerTest {

    @Mock
    private IAiAssistantService aiAssistantService;

    @InjectMocks
    private AiAssistantController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testProcessChat_Success() throws Exception {
        ChatRequestDto requestDto = new ChatRequestDto("CUST_101", "How much did I spend on Dining?");
        ChatResponseDto responseDto = new ChatResponseDto(
                "CHAT_001",
                "[Account Facts]\nYour total spending in 'Dining' is ₹2,500.00 across 3 transaction(s).\n\n[Guidance]\nMonitoring category-level expenses helps maintain budget goals.",
                LocalDateTime.now(),
                List.of("transactions")
        );

        when(aiAssistantService.processChat(any(ChatRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatId").value("CHAT_001"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Account Facts")))
                .andExpect(jsonPath("$.dataSourcesUsed").isArray())
                .andExpect(jsonPath("$.dataSourcesUsed[0]").value("transactions"));

        verify(aiAssistantService, times(1)).processChat(any(ChatRequestDto.class));
    }

    @Test
    void testProcessChat_CustomerNotFound_Returns404() throws Exception {
        ChatRequestDto requestDto = new ChatRequestDto("CUST_UNKNOWN", "Show my recent transactions");

        when(aiAssistantService.processChat(any(ChatRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Customer not found with ID: CUST_UNKNOWN"));

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: CUST_UNKNOWN"));

        verify(aiAssistantService, times(1)).processChat(any(ChatRequestDto.class));
    }

    @Test
    void testProcessChat_Forbidden_Returns403() throws Exception {
        ChatRequestDto requestDto = new ChatRequestDto("CUST_OTHER", "What is my credit utilization?");

        when(aiAssistantService.processChat(any(ChatRequestDto.class)))
                .thenThrow(new AccessDeniedException("You are not authorized to access this resource"));

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized to access this resource"));

        verify(aiAssistantService, times(1)).processChat(any(ChatRequestDto.class));
    }

    @Test
    void testProcessChat_BlankMessage_Returns400() throws Exception {
        ChatRequestDto requestDto = new ChatRequestDto("CUST_101", "");

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(aiAssistantService);
    }
}
