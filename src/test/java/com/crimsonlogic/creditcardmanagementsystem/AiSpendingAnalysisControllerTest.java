package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AiSpendingAnalysisController;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiSpendingAnalysisResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiSpendingAnalysisService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiSpendingAnalysisControllerTest {

    @Mock
    private IAiSpendingAnalysisService aiSpendingAnalysisService;

    @InjectMocks
    private AiSpendingAnalysisController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetSpendingAnalysis_Success() throws Exception {
        AiSpendingAnalysisResponseDto responseDto = new AiSpendingAnalysisResponseDto(
                "CUST1001",
                new BigDecimal("6500.00"),
                Map.of("Dining", 38.46, "Travel", 46.15, "Shopping", 15.38),
                List.of("MakeMyTrip", "Swiggy", "Amazon"),
                new BigDecimal("1625.00"),
                30.0,
                "Strong spending in travel and dining.",
                "INS100001",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        when(aiSpendingAnalysisService.getSpendingAnalysis("CUST1001")).thenReturn(responseDto);

        mockMvc.perform(get("/api/ai/spending-analysis/CUST1001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST1001"))
                .andExpect(jsonPath("$.totalSpending").value(6500.00))
                .andExpect(jsonPath("$.averageTransactionValue").value(1625.00))
                .andExpect(jsonPath("$.monthOverMonthChangePercent").value(30.0))
                .andExpect(jsonPath("$.insightId").value("INS100001"))
                .andExpect(jsonPath("$.topMerchants[0]").value("MakeMyTrip"));

        verify(aiSpendingAnalysisService, times(1)).getSpendingAnalysis("CUST1001");
    }

    @Test
    void testGetSpendingAnalysis_CustomerNotFound_Returns404() throws Exception {
        when(aiSpendingAnalysisService.getSpendingAnalysis("CUST_UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Customer not found with ID: CUST_UNKNOWN"));

        mockMvc.perform(get("/api/ai/spending-analysis/CUST_UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: CUST_UNKNOWN"));

        verify(aiSpendingAnalysisService, times(1)).getSpendingAnalysis("CUST_UNKNOWN");
    }

    @Test
    void testGetSpendingAnalysis_Forbidden_Returns403() throws Exception {
        when(aiSpendingAnalysisService.getSpendingAnalysis("CUST1002"))
                .thenThrow(new AccessDeniedException("You are not authorized to access this resource"));

        mockMvc.perform(get("/api/ai/spending-analysis/CUST1002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized to access this resource"));

        verify(aiSpendingAnalysisService, times(1)).getSpendingAnalysis("CUST1002");
    }
}
