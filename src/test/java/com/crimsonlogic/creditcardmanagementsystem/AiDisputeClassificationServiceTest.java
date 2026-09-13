package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeType;
import com.crimsonlogic.creditcardmanagementsystem.service.AiDisputeClassificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiDisputeClassificationServiceTest {

    @Mock
    private GeminiApiClient geminiApiClient;

    @InjectMocks
    private AiDisputeClassificationServiceImpl aiDisputeClassificationService;

    @Test
    void testClassifyDispute_DuplicateChargeWording_ReturnsDuplicateTransaction() {
        // Fallback test with clear duplicate wording
        when(geminiApiClient.classifyDispute(anyString())).thenReturn(Optional.empty());

        DisputeClassificationRequestDto request = new DisputeClassificationRequestDto(
                "I was charged twice for my coffee at Starbucks on Monday morning."
        );

        DisputeClassificationResponseDto response = aiDisputeClassificationService.classifyDispute(request);

        assertNotNull(response);
        assertEquals(DisputeType.DUPLICATE_TRANSACTION, response.getSuggestedType());
        assertEquals("LOW", response.getConfidence());
        assertTrue(response.getExplanation().contains("duplicate transaction"));
    }

    @Test
    void testClassifyDispute_MissingRefundWording_ReturnsRefundNotReceived() {
        when(geminiApiClient.classifyDispute(anyString())).thenReturn(Optional.empty());

        DisputeClassificationRequestDto request = new DisputeClassificationRequestDto(
                "Returned the shoes two weeks ago but the refund didn't receive from merchant."
        );

        DisputeClassificationResponseDto response = aiDisputeClassificationService.classifyDispute(request);

        assertNotNull(response);
        assertEquals(DisputeType.REFUND_NOT_RECEIVED, response.getSuggestedType());
        assertEquals("LOW", response.getConfidence());
        assertTrue(response.getExplanation().contains("refund"));
    }

    @Test
    void testClassifyDispute_GibberishText_ReturnsOtherWithLowConfidence() {
        when(geminiApiClient.classifyDispute(anyString())).thenReturn(Optional.empty());

        DisputeClassificationRequestDto request = new DisputeClassificationRequestDto(
                "asdfghjk 123456 qwerty foo bar"
        );

        DisputeClassificationResponseDto response = aiDisputeClassificationService.classifyDispute(request);

        assertNotNull(response);
        assertEquals(DisputeType.OTHER, response.getSuggestedType());
        assertEquals("LOW", response.getConfidence());
        assertTrue(response.getExplanation().contains("Other"));
    }

    @Test
    void testClassifyDispute_GeminiSuccess_ReturnsHighConfidence() {
        GeminiApiClient.DisputeClassificationResult aiResult = new GeminiApiClient.DisputeClassificationResult(
                DisputeType.UNAUTHORIZED_TRANSACTION,
                "Customer reports card was stolen and unauthorized charges were incurred."
        );
        when(geminiApiClient.classifyDispute("My card was stolen and someone made fraudulent purchases."))
                .thenReturn(Optional.of(aiResult));

        DisputeClassificationRequestDto request = new DisputeClassificationRequestDto(
                "My card was stolen and someone made fraudulent purchases."
        );

        DisputeClassificationResponseDto response = aiDisputeClassificationService.classifyDispute(request);

        assertNotNull(response);
        assertEquals(DisputeType.UNAUTHORIZED_TRANSACTION, response.getSuggestedType());
        assertEquals("HIGH", response.getConfidence());
        assertEquals("Customer reports card was stolen and unauthorized charges were incurred.", response.getExplanation());
    }

    @Test
    void testClassifyDispute_WrongAmountWording_ReturnsWrongAmount() {
        when(geminiApiClient.classifyDispute(anyString())).thenReturn(Optional.empty());

        DisputeClassificationRequestDto request = new DisputeClassificationRequestDto(
                "The restaurant bill was ₹500 but they charged wrong amount ₹5000 on my card."
        );

        DisputeClassificationResponseDto response = aiDisputeClassificationService.classifyDispute(request);

        assertNotNull(response);
        assertEquals(DisputeType.WRONG_AMOUNT, response.getSuggestedType());
        assertEquals("LOW", response.getConfidence());
    }

    @Test
    void testClassifyDispute_MerchantDisputeWording_ReturnsMerchantDispute() {
        when(geminiApiClient.classifyDispute(anyString())).thenReturn(Optional.empty());

        DisputeClassificationRequestDto request = new DisputeClassificationRequestDto(
                "The merchant delivered damaged and broken items and refuses to address the issue."
        );

        DisputeClassificationResponseDto response = aiDisputeClassificationService.classifyDispute(request);

        assertNotNull(response);
        assertEquals(DisputeType.MERCHANT_DISPUTE, response.getSuggestedType());
        assertEquals("LOW", response.getConfidence());
    }
}
