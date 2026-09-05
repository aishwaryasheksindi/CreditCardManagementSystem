package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.CardController;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardActionReasonDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SetPinRequest;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private ICardService cardService;

    @InjectMocks
    private CardController cardController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testSetPin_ValidPin_Success() throws Exception {
        SetPinRequest request = new SetPinRequest("1234");

        doNothing().when(cardService).setPin("CARD1001", "1234");

        mockMvc.perform(post("/api/cards/CARD1001/set-pin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PIN set successfully"));

        verify(cardService, times(1)).setPin("CARD1001", "1234");
    }

    @Test
    void testSetPin_InvalidPin_FailsValidation() throws Exception {
        SetPinRequest request = new SetPinRequest("123"); // 3 digits instead of 4

        mockMvc.perform(post("/api/cards/CARD1001/set-pin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).setPin(any(), any());
    }

    @Test
    void testVerifyPin_CorrectPin_ReturnsTrue() throws Exception {
        SetPinRequest request = new SetPinRequest("1234");

        when(cardService.verifyPin("CARD1001", "1234")).thenReturn(true);

        mockMvc.perform(post("/api/cards/CARD1001/verify-pin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));

        verify(cardService, times(1)).verifyPin("CARD1001", "1234");
    }

    @Test
    void testVerifyPin_IncorrectPin_ReturnsBadRequest() throws Exception {
        SetPinRequest request = new SetPinRequest("9999");

        when(cardService.verifyPin("CARD1001", "9999"))
                .thenThrow(new IllegalArgumentException("Incorrect PIN"));

        mockMvc.perform(post("/api/cards/CARD1001/verify-pin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incorrect PIN"));

        verify(cardService, times(1)).verifyPin("CARD1001", "9999");
    }

    @Test
    void testUnblockCard_ValidReason_Success() throws Exception {
        CardActionReasonDto request = new CardActionReasonDto("Customer confirmed identity");
        CardResponseDto responseDto = new CardResponseDto();
        responseDto.setCardId("CARD1001");
        responseDto.setCardStatus(CardStatus.ACTIVE);

        when(cardService.unblockCard("CARD1001", "Customer confirmed identity")).thenReturn(responseDto);

        mockMvc.perform(post("/api/cards/CARD1001/unblock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value("CARD1001"))
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"));

        verify(cardService, times(1)).unblockCard("CARD1001", "Customer confirmed identity");
    }

    @Test
    void testUnblockCard_BlankReason_FailsValidation() throws Exception {
        CardActionReasonDto request = new CardActionReasonDto("");

        mockMvc.perform(post("/api/cards/CARD1001/unblock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).unblockCard(any(), any());
    }

    @Test
    void testReplaceCard_ValidReason_Success() throws Exception {
        CardActionReasonDto request = new CardActionReasonDto("Card stolen while travelling");
        CardResponseDto responseDto = new CardResponseDto();
        responseDto.setCardId("CARD1002");
        responseDto.setCardStatus(CardStatus.ACTIVE);

        when(cardService.replaceCard("CARD1001", "Card stolen while travelling")).thenReturn(responseDto);

        mockMvc.perform(post("/api/cards/CARD1001/replace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardId").value("CARD1002"))
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"));

        verify(cardService, times(1)).replaceCard("CARD1001", "Card stolen while travelling");
    }

    @Test
    void testReplaceCard_BlankReason_FailsValidation() throws Exception {
        CardActionReasonDto request = new CardActionReasonDto("   ");

        mockMvc.perform(post("/api/cards/CARD1001/replace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).replaceCard(any(), any());
    }
}
