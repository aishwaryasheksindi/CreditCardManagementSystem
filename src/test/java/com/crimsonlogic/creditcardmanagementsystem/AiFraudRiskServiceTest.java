package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiFraudRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.AiFraudRiskServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.service.IFraudAlertService;
import com.crimsonlogic.creditcardmanagementsystem.service.IRiskScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiFraudRiskServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IRiskScoreService riskScoreService;

    @Mock
    private IFraudAlertService fraudAlertService;

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private AiFraudRiskServiceImpl aiFraudRiskService;

    private Customer sampleCustomer;
    private Card sampleCard;
    private TransactionCategory diningCategory;
    private TransactionCategory travelCategory;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId("CUST1001");
        sampleCustomer.setName("Jane Doe");

        sampleCard = new Card();
        sampleCard.setCardId("CARD1001");
        sampleCard.setCustomer(sampleCustomer);

        diningCategory = new TransactionCategory("CAT1001", "Dining", "Restaurant & Food");
        travelCategory = new TransactionCategory("CAT1002", "Travel", "Airlines & Hotels");
    }

    private List<Transaction> createHistoricalTransactions(int count, BigDecimal amount, String location, TransactionCategory category) {
        List<Transaction> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Transaction t = new Transaction();
            t.setTransactionId("TXN_HIST_" + i);
            t.setCard(sampleCard);
            t.setAmount(amount);
            t.setTransactionLocation(location);
            t.setCategory(category);
            t.setTransactionDate(LocalDateTime.now().minusDays(i));
            list.add(t);
        }
        return list;
    }

    @Test
    void testGetFraudRisk_NormalPurchase_LowRiskScore() {
        // Customer average is 1000.00 in Delhi with Dining
        List<Transaction> history = createHistoricalTransactions(10, new BigDecimal("1000.00"), "Delhi", diningCategory);

        Transaction currentTxn = new Transaction();
        currentTxn.setTransactionId("TXN200001");
        currentTxn.setCard(sampleCard);
        currentTxn.setAmount(new BigDecimal("1100.00")); // 1.1x average (normal)
        currentTxn.setTransactionLocation("Delhi");        // Known location
        currentTxn.setCategory(diningCategory);             // Known category
        currentTxn.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.findById("TXN200001")).thenReturn(Optional.of(currentTxn));
        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST1001"))
                .thenReturn(history);
        when(transactionRepository.findByCard_CardIdAndTransactionDateBetween(eq("CARD1001"), any(), any()))
                .thenReturn(Collections.emptyList()); // 0 velocity in 1h window
        when(geminiApiClient.getFraudExplanation(anyInt(), anyString()))
                .thenReturn(Optional.of("Transaction appears typical for this cardholder's routine spending."));

        RiskScoreResponseDto savedRiskScoreDto = new RiskScoreResponseDto(
                "RS100001", "TXN200001", 0, "LOW", "v1.0-ai", LocalDateTime.now(), "Normal transaction parameters; no significant risk factors detected"
        );
        when(riskScoreService.createRiskScore(any(RiskScoreRequestDto.class))).thenReturn(savedRiskScoreDto);

        AiFraudRiskResponseDto result = aiFraudRiskService.getFraudRisk("TXN200001");

        assertNotNull(result);
        assertEquals("TXN200001", result.getTransactionId());
        assertEquals(0, result.getScore());
        assertEquals("LOW", result.getRiskLevel());
        assertFalse(result.isFraudAlertCreated());
        assertEquals("RS100001", result.getRiskScoreId());
        assertEquals("Transaction appears typical for this cardholder's routine spending.", result.getExplanation());

        // Verify FraudAlert was NOT created
        verify(fraudAlertService, never()).createFraudAlert(any());
        verify(riskScoreService, times(1)).createRiskScore(any(RiskScoreRequestDto.class));
    }

    @Test
    void testGetFraudRisk_OutlierPurchase_HighRisk_CreatesFraudAlert() {
        // Customer average is 1000.00 in Delhi with Dining
        List<Transaction> history = createHistoricalTransactions(10, new BigDecimal("1000.00"), "Delhi", diningCategory);

        // Outlier purchase: 5x average (5000.00) in a new location (Mumbai)
        Transaction outlierTxn = new Transaction();
        outlierTxn.setTransactionId("TXN200002");
        outlierTxn.setCard(sampleCard);
        outlierTxn.setAmount(new BigDecimal("5000.00")); // 5.0x average -> 45 points
        outlierTxn.setTransactionLocation("Mumbai");        // New location -> 30 points
        outlierTxn.setCategory(diningCategory);
        outlierTxn.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.findById("TXN200002")).thenReturn(Optional.of(outlierTxn));
        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST1001"))
                .thenReturn(history);
        when(transactionRepository.findByCard_CardIdAndTransactionDateBetween(eq("CARD1001"), any(), any()))
                .thenReturn(Collections.emptyList());
        when(geminiApiClient.getFraudExplanation(eq(75), anyString()))
                .thenReturn(Optional.of("Transaction amount is 5x customer average in an unfamiliar location (Mumbai)."));

        RiskScoreResponseDto savedRiskScoreDto = new RiskScoreResponseDto(
                "RS100002", "TXN200002", 75, "HIGH", "v1.0-ai", LocalDateTime.now(), "Amount 5.0x customer average; New location: Mumbai"
        );
        when(riskScoreService.createRiskScore(any(RiskScoreRequestDto.class))).thenReturn(savedRiskScoreDto);

        AiFraudRiskResponseDto result = aiFraudRiskService.getFraudRisk("TXN200002");

        assertNotNull(result);
        assertEquals("TXN200002", result.getTransactionId());
        assertEquals(75, result.getScore());
        assertEquals("HIGH", result.getRiskLevel());
        assertTrue(result.isFraudAlertCreated());
        assertEquals("RS100002", result.getRiskScoreId());
        assertTrue(result.getRiskFactors().contains("Amount 5.0x customer average"));
        assertTrue(result.getRiskFactors().contains("New location: Mumbai"));

        // Verify FraudAlert was created with status OPEN and the explanation
        ArgumentCaptor<FraudAlertRequestDto> alertCaptor = ArgumentCaptor.forClass(FraudAlertRequestDto.class);
        verify(fraudAlertService, times(1)).createFraudAlert(alertCaptor.capture());
        FraudAlertRequestDto capturedAlert = alertCaptor.getValue();
        assertEquals("TXN200002", capturedAlert.getTransactionId());
        assertEquals("RS100002", capturedAlert.getRiskScoreId());
        assertEquals("OPEN", capturedAlert.getStatus());
        assertEquals("Transaction amount is 5x customer average in an unfamiliar location (Mumbai).", capturedAlert.getReason());
        assertNull(capturedAlert.getInvestigatorStaffId());
    }

    @Test
    void testGetFraudRisk_GeminiFallback_UsesRiskFactorsDirectly() {
        List<Transaction> history = createHistoricalTransactions(10, new BigDecimal("1000.00"), "Delhi", diningCategory);

        Transaction outlierTxn = new Transaction();
        outlierTxn.setTransactionId("TXN200003");
        outlierTxn.setCard(sampleCard);
        outlierTxn.setAmount(new BigDecimal("3000.00")); // 3.0x average -> 30 pts
        outlierTxn.setTransactionLocation("Delhi");
        outlierTxn.setCategory(travelCategory);             // New category -> 10 pts
        outlierTxn.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.findById("TXN200003")).thenReturn(Optional.of(outlierTxn));
        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST1001"))
                .thenReturn(history);
        when(transactionRepository.findByCard_CardIdAndTransactionDateBetween(eq("CARD1001"), any(), any()))
                .thenReturn(Collections.emptyList());
        // Gemini fails/unavailable -> returns Optional.empty()
        when(geminiApiClient.getFraudExplanation(eq(40), anyString())).thenReturn(Optional.empty());

        RiskScoreResponseDto savedRiskScoreDto = new RiskScoreResponseDto(
                "RS100003", "TXN200003", 40, "MEDIUM", "v1.0-ai", LocalDateTime.now(), "Amount 3.0x customer average; New merchant category: Travel"
        );
        when(riskScoreService.createRiskScore(any(RiskScoreRequestDto.class))).thenReturn(savedRiskScoreDto);

        AiFraudRiskResponseDto result = aiFraudRiskService.getFraudRisk("TXN200003");

        assertNotNull(result);
        assertEquals(40, result.getScore());
        assertEquals("MEDIUM", result.getRiskLevel());
        assertFalse(result.isFraudAlertCreated());
        // Graceful fallback: explanation equals riskFactors string
        assertEquals(result.getRiskFactors(), result.getExplanation());
    }

    @Test
    void testGetFraudRisk_NotFound_ThrowsResourceNotFoundException() {
        when(transactionRepository.findById("TXN_NONEXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> aiFraudRiskService.getFraudRisk("TXN_NONEXISTENT"));
    }

    @Test
    void testGetFraudRisk_UnauthorizedUser_ThrowsAccessDeniedException() {
        Transaction txn = new Transaction();
        txn.setTransactionId("TXN200004");
        txn.setCard(sampleCard);

        when(transactionRepository.findById("TXN200004")).thenReturn(Optional.of(txn));
        doThrow(new AccessDeniedException("You are not authorized to access this resource"))
                .when(currentUserContext).assertCustomerOwnership("CUST1001");

        assertThrows(AccessDeniedException.class, () -> aiFraudRiskService.getFraudRisk("TXN200004"));
    }
}
