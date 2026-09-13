package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AnomalyDetectionController;
import com.crimsonlogic.creditcardmanagementsystem.dto.AnomalyDetectionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.AnomalyFlag;
import com.crimsonlogic.creditcardmanagementsystem.enums.AnomalyType;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.service.IAnomalyDetectionService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnomalyDetectionControllerTest {

    @Mock
    private IAnomalyDetectionService anomalyDetectionService;

    @InjectMocks
    private AnomalyDetectionController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAnomalies_Success() throws Exception {
        AnomalyFlag flag1 = new AnomalyFlag(
                AnomalyType.SPENDING_SPIKE,
                "Recent transaction amount ₹15000.00 is 3.5x higher than historical average",
                "TXN_101",
                "MEDIUM",
                LocalDateTime.now()
        );
        AnomalyFlag flag2 = new AnomalyFlag(
                AnomalyType.UNUSUAL_LOCATION,
                "Transaction location 'Dubai' differs from customer's typical historical locations.",
                "TXN_102",
                "MEDIUM",
                LocalDateTime.now()
        );

        AnomalyDetectionResponseDto responseDto = new AnomalyDetectionResponseDto(
                "CUST_ANOMALY",
                LocalDateTime.now(),
                List.of(flag1, flag2)
        );

        when(anomalyDetectionService.detectAnomalies("CUST_ANOMALY")).thenReturn(responseDto);

        mockMvc.perform(get("/api/ai/anomaly-detection/CUST_ANOMALY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST_ANOMALY"))
                .andExpect(jsonPath("$.flags").isArray())
                .andExpect(jsonPath("$.flags[0].type").value("SPENDING_SPIKE"))
                .andExpect(jsonPath("$.flags[0].severity").value("MEDIUM"))
                .andExpect(jsonPath("$.flags[0].relatedTransactionId").value("TXN_101"))
                .andExpect(jsonPath("$.flags[1].type").value("UNUSUAL_LOCATION"))
                .andExpect(jsonPath("$.flags[1].relatedTransactionId").value("TXN_102"));

        verify(anomalyDetectionService, times(1)).detectAnomalies("CUST_ANOMALY");
    }

    @Test
    void testGetAnomalies_CustomerNotFound_Returns404() throws Exception {
        when(anomalyDetectionService.detectAnomalies("CUST_UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Customer not found with ID: CUST_UNKNOWN"));

        mockMvc.perform(get("/api/ai/anomaly-detection/CUST_UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: CUST_UNKNOWN"));

        verify(anomalyDetectionService, times(1)).detectAnomalies("CUST_UNKNOWN");
    }

    @Test
    void testGetAnomalies_Forbidden_Returns403() throws Exception {
        when(anomalyDetectionService.detectAnomalies("CUST_FORBIDDEN"))
                .thenThrow(new AccessDeniedException("You are not authorized to access this resource"));

        mockMvc.perform(get("/api/ai/anomaly-detection/CUST_FORBIDDEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized to access this resource"));

        verify(anomalyDetectionService, times(1)).detectAnomalies("CUST_FORBIDDEN");
    }
}
