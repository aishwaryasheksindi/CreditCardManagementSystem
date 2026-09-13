package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AiDisputeClassificationController;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeType;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiDisputeClassificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiDisputeClassificationControllerTest {

    @Mock
    private IAiDisputeClassificationService aiDisputeClassificationService;

    @InjectMocks
    private AiDisputeClassificationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testClassifyDispute_Success() throws Exception {
        DisputeClassificationResponseDto responseDto = new DisputeClassificationResponseDto(
                DisputeType.DUPLICATE_TRANSACTION,
                "HIGH",
                "Transaction was processed twice for the exact same amount."
        );

        when(aiDisputeClassificationService.classifyDispute(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/ai/dispute-classification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"I was billed twice for my purchase.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedType").value("DUPLICATE_TRANSACTION"))
                .andExpect(jsonPath("$.confidence").value("HIGH"))
                .andExpect(jsonPath("$.explanation").value("Transaction was processed twice for the exact same amount."));

        verify(aiDisputeClassificationService, times(1)).classifyDispute(any());
    }

    @Test
    void testClassifyDispute_BlankDescription_Returns400() throws Exception {
        mockMvc.perform(post("/api/ai/dispute-classification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"\"}"))
                .andExpect(status().isBadRequest());

        verify(aiDisputeClassificationService, never()).classifyDispute(any());
    }
}
