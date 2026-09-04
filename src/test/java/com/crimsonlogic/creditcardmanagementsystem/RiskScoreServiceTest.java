package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.RiskScore;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RiskScoreRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.RiskScoreServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskScoreServiceTest {

    @Mock
    private RiskScoreRepository riskScoreRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RiskScoreServiceImpl riskScoreService;

    @Test
    void testCreateRiskScore_DerivesLowRisk() {
        String txnId = "TXN100001";
        when(transactionRepository.existsById(txnId)).thenReturn(true);
        when(riskScoreRepository.existsById(any())).thenReturn(false);
        when(riskScoreRepository.save(any(RiskScore.class))).thenAnswer(inv -> inv.getArgument(0));

        RiskScoreRequestDto requestDto = new RiskScoreRequestDto(txnId, 20, "v1.0", "Normal spend pattern");

        RiskScoreResponseDto result = riskScoreService.createRiskScore(requestDto);

        assertNotNull(result);
        assertEquals("LOW", result.getRiskLevel());
        assertEquals(20, result.getScore());
        assertTrue(result.getRiskScoreId().startsWith("RS"));
    }

    @Test
    void testCreateRiskScore_DerivesMediumRisk() {
        String txnId = "TXN100002";
        when(transactionRepository.existsById(txnId)).thenReturn(true);
        when(riskScoreRepository.existsById(any())).thenReturn(false);
        when(riskScoreRepository.save(any(RiskScore.class))).thenAnswer(inv -> inv.getArgument(0));

        RiskScoreRequestDto requestDto = new RiskScoreRequestDto(txnId, 55, "v1.0", "Foreign IP, off-hours");

        RiskScoreResponseDto result = riskScoreService.createRiskScore(requestDto);

        assertNotNull(result);
        assertEquals("MEDIUM", result.getRiskLevel());
        assertEquals(55, result.getScore());
    }

    @Test
    void testCreateRiskScore_DerivesHighRisk() {
        String txnId = "TXN100003";
        when(transactionRepository.existsById(txnId)).thenReturn(true);
        when(riskScoreRepository.existsById(any())).thenReturn(false);
        when(riskScoreRepository.save(any(RiskScore.class))).thenAnswer(inv -> inv.getArgument(0));

        RiskScoreRequestDto requestDto = new RiskScoreRequestDto(txnId, 90, "v1.0", "Unusual velocity, high amount");

        RiskScoreResponseDto result = riskScoreService.createRiskScore(requestDto);

        assertNotNull(result);
        assertEquals("HIGH", result.getRiskLevel());
        assertEquals(90, result.getScore());
    }

    @Test
    void testCreateRiskScore_ThrowsResourceNotFoundException_WhenTransactionNotFound() {
        String txnId = "TXN999999";
        when(transactionRepository.existsById(txnId)).thenReturn(false);

        RiskScoreRequestDto requestDto = new RiskScoreRequestDto(txnId, 45, "v1.0", null);

        assertThrows(ResourceNotFoundException.class, () ->
                riskScoreService.createRiskScore(requestDto)
        );
        verify(riskScoreRepository, never()).save(any());
    }
}
