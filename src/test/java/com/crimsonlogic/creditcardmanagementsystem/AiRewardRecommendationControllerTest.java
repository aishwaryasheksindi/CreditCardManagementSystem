package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AiRewardRecommendationController;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiRewardRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiRewardRecommendationService;
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
class AiRewardRecommendationControllerTest {

    @Mock
    private IAiRewardRecommendationService aiRewardRecommendationService;

    @InjectMocks
    private AiRewardRecommendationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetRewardRecommendation_Success() throws Exception {
        AiRewardRecommendationResponseDto responseDto = new AiRewardRecommendationResponseDto(
                "CUST1001",
                true,
                "TravelRewards",
                "You spend heavily on Travel and your Platinum card provides exclusive Travel rewards.",
                "CARD_PLATINUM"
        );

        when(aiRewardRecommendationService.getRewardRecommendation("CUST1001")).thenReturn(responseDto);

        mockMvc.perform(get("/api/ai/reward-recommendation/CUST1001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST1001"))
                .andExpect(jsonPath("$.hasRecommendation").value(true))
                .andExpect(jsonPath("$.offerName").value("TravelRewards"))
                .andExpect(jsonPath("$.matchedCardId").value("CARD_PLATINUM"));

        verify(aiRewardRecommendationService, times(1)).getRewardRecommendation("CUST1001");
    }

    @Test
    void testGetRewardRecommendation_NotFound_Returns404() throws Exception {
        when(aiRewardRecommendationService.getRewardRecommendation("CUST_UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Customer not found with ID: CUST_UNKNOWN"));

        mockMvc.perform(get("/api/ai/reward-recommendation/CUST_UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: CUST_UNKNOWN"));

        verify(aiRewardRecommendationService, times(1)).getRewardRecommendation("CUST_UNKNOWN");
    }

    @Test
    void testGetRewardRecommendation_Forbidden_Returns403() throws Exception {
        when(aiRewardRecommendationService.getRewardRecommendation("CUST_FORBIDDEN"))
                .thenThrow(new AccessDeniedException("You are not authorized to access this resource"));

        mockMvc.perform(get("/api/ai/reward-recommendation/CUST_FORBIDDEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You are not authorized to access this resource"));

        verify(aiRewardRecommendationService, times(1)).getRewardRecommendation("CUST_FORBIDDEN");
    }
}
