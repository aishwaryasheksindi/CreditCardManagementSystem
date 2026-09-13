package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AiPaymentRiskController;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiPaymentRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiPaymentRiskService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiPaymentRiskControllerTest {

    @Mock
    private IAiPaymentRiskService aiPaymentRiskService;

    @InjectMocks
    private AiPaymentRiskController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetPaymentRisk_Success() throws Exception {
        AiPaymentRiskResponseDto responseDto = new AiPaymentRiskResponseDto(
                "CUST_RISKY",
                "HIGH",
                80.0,
                2,
                1,
                "High payment risk detected due to high utilization and delinquent history."
        );

        when(aiPaymentRiskService.getPaymentRisk("CUST_RISKY")).thenReturn(responseDto);

        mockMvc.perform(get("/api/ai/payment-risk/CUST_RISKY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST_RISKY"))
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.utilizationPercent").value(80.0))
                .andExpect(jsonPath("$.minimumOnlyCycleCount").value(2))
                .andExpect(jsonPath("$.lateCycleCount").value(1));

        verify(aiPaymentRiskService, times(1)).getPaymentRisk("CUST_RISKY");
    }

    @Test
    void testGetPaymentRisk_CustomerNotFound_Returns404() throws Exception {
        when(aiPaymentRiskService.getPaymentRisk("CUST_UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Customer not found with ID: CUST_UNKNOWN"));

        mockMvc.perform(get("/api/ai/payment-risk/CUST_UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: CUST_UNKNOWN"));

        verify(aiPaymentRiskService, times(1)).getPaymentRisk("CUST_UNKNOWN");
    }

    @Test
    void testGetPaymentRisk_Forbidden_Returns403() throws Exception {
        when(aiPaymentRiskService.getPaymentRisk("CUST_FORBIDDEN"))
                .thenThrow(new AccessDeniedException("You are not authorized to access this resource"));

        mockMvc.perform(get("/api/ai/payment-risk/CUST_FORBIDDEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized to access this resource"));

        verify(aiPaymentRiskService, times(1)).getPaymentRisk("CUST_FORBIDDEN");
    }
}
