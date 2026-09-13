package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AiEmiRecommendationController;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiEmiRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiTenureOptionDto;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiEmiRecommendationService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiEmiRecommendationControllerTest {

    @Mock
    private IAiEmiRecommendationService aiEmiRecommendationService;

    @InjectMocks
    private AiEmiRecommendationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetEmiRecommendation_Success() throws Exception {
        EmiTenureOptionDto option6 = new EmiTenureOptionDto(6, new BigDecimal("2088.66"), new BigDecimal("531.96"), new BigDecimal("12711.96"), new BigDecimal("180.00"));
        AiEmiRecommendationResponseDto responseDto = new AiEmiRecommendationResponseDto(
                "TXN100001",
                new BigDecimal("12000.00"),
                "5678",
                true,
                "Transaction eligible for EMI",
                6,
                new BigDecimal("2088.66"),
                new BigDecimal("531.96"),
                new BigDecimal("12711.96"),
                new BigDecimal("180.00"),
                new BigDecimal("15.00"),
                "6-month tenure provides optimal savings.",
                true,
                List.of(option6)
        );

        when(aiEmiRecommendationService.getEmiRecommendation("TXN100001")).thenReturn(responseDto);

        mockMvc.perform(get("/api/ai/emi-recommendation/TXN100001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TXN100001"))
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.recommendedTenureMonths").value(6))
                .andExpect(jsonPath("$.cardLast4").value("5678"))
                .andExpect(jsonPath("$.aiGenerated").value(true));

        verify(aiEmiRecommendationService, times(1)).getEmiRecommendation("TXN100001");
    }

    @Test
    void testGetEmiRecommendation_NotFound_Returns404() throws Exception {
        when(aiEmiRecommendationService.getEmiRecommendation("TXN_UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Transaction not found with ID: TXN_UNKNOWN"));

        mockMvc.perform(get("/api/ai/emi-recommendation/TXN_UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Transaction not found with ID: TXN_UNKNOWN"));

        verify(aiEmiRecommendationService, times(1)).getEmiRecommendation("TXN_UNKNOWN");
    }

    @Test
    void testGetEmiRecommendation_IneligibleAmount_Returns400() throws Exception {
        when(aiEmiRecommendationService.getEmiRecommendation("TXN100002"))
                .thenThrow(new IllegalArgumentException("Transaction amount must be at least ₹3000 to be eligible for EMI conversion"));

        mockMvc.perform(get("/api/ai/emi-recommendation/TXN100002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Transaction amount must be at least ₹3000 to be eligible for EMI conversion"));

        verify(aiEmiRecommendationService, times(1)).getEmiRecommendation("TXN100002");
    }

    @Test
    void testGetEmiRecommendation_UnauthorizedCustomer_Returns403() throws Exception {
        when(aiEmiRecommendationService.getEmiRecommendation("TXN100003"))
                .thenThrow(new AccessDeniedException("You are not authorized to access this resource"));

        mockMvc.perform(get("/api/ai/emi-recommendation/TXN100003")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized to access this resource"));

        verify(aiEmiRecommendationService, times(1)).getEmiRecommendation("TXN100003");
    }
}
