package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AiFraudRiskController;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiFraudRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiFraudRiskService;
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
class AiFraudRiskControllerTest {

    @Mock
    private IAiFraudRiskService aiFraudRiskService;

    @InjectMocks
    private AiFraudRiskController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetFraudRisk_Success() throws Exception {
        AiFraudRiskResponseDto responseDto = new AiFraudRiskResponseDto(
                "TXN200001",
                75,
                "HIGH",
                "Amount 5.0x customer average; New location: Mumbai",
                "Transaction amount is 5x customer average in an unfamiliar location (Mumbai).",
                "RS100001",
                true
        );

        when(aiFraudRiskService.getFraudRisk("TXN200001")).thenReturn(responseDto);

        mockMvc.perform(get("/api/ai/fraud-risk/TXN200001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TXN200001"))
                .andExpect(jsonPath("$.score").value(75))
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.riskScoreId").value("RS100001"))
                .andExpect(jsonPath("$.fraudAlertCreated").value(true))
                .andExpect(jsonPath("$.explanation").value("Transaction amount is 5x customer average in an unfamiliar location (Mumbai)."));

        verify(aiFraudRiskService, times(1)).getFraudRisk("TXN200001");
    }

    @Test
    void testGetFraudRisk_NotFound_Returns404() throws Exception {
        when(aiFraudRiskService.getFraudRisk("TXN_UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Transaction not found with ID: TXN_UNKNOWN"));

        mockMvc.perform(get("/api/ai/fraud-risk/TXN_UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Transaction not found with ID: TXN_UNKNOWN"));

        verify(aiFraudRiskService, times(1)).getFraudRisk("TXN_UNKNOWN");
    }

    @Test
    void testGetFraudRisk_UnauthorizedCustomer_Returns403() throws Exception {
        when(aiFraudRiskService.getFraudRisk("TXN_FORBIDDEN"))
                .thenThrow(new AccessDeniedException("You are not authorized to access this resource"));

        mockMvc.perform(get("/api/ai/fraud-risk/TXN_FORBIDDEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized to access this resource"));

        verify(aiFraudRiskService, times(1)).getFraudRisk("TXN_FORBIDDEN");
    }
}
