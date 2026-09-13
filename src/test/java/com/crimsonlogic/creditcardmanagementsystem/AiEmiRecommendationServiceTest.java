package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiEmiRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.EmiRecommendation;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.EmiRecommendationRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.AiEmiRecommendationServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.utility.EmiCalculatorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiEmiRecommendationServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EmiRecommendationRepository emiRecommendationRepository;

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private AiEmiRecommendationServiceImpl emiRecommendationService;

    private Transaction sampleTransaction;
    private Card sampleCard;
    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId("CUST1001");
        sampleCustomer.setName("Jane Doe");

        CardType cardType = new CardType();
        cardType.setTypeName("Gold");
        cardType.setEmiEligible(true);
        cardType.setInterestRate(new BigDecimal("15.00"));

        sampleCard = new Card();
        sampleCard.setCardId("CARD1001");
        sampleCard.setCardReference("CARD-5678");
        sampleCard.setCardStatus(CardStatus.ACTIVE);
        sampleCard.setCustomer(sampleCustomer);
        sampleCard.setCardType(cardType);
        sampleCard.setAvailableLimit(new BigDecimal("45000.00"));
        sampleCard.setInterestRate(new BigDecimal("15.00"));

        sampleTransaction = new Transaction();
        sampleTransaction.setTransactionId("TXN100001");
        sampleTransaction.setCard(sampleCard);
        sampleTransaction.setAmount(new BigDecimal("12000.00"));
        sampleTransaction.setTransactionType(TransactionType.PURCHASE);
    }

    @Test
    void testGetEmiRecommendation_Success_WithAiReasoning() {
        when(transactionRepository.findById("TXN100001")).thenReturn(Optional.of(sampleTransaction));
        when(geminiApiClient.getEmiRecommendation(any(), any(), any()))
                .thenReturn(Optional.of(new GeminiApiClient.AiRecommendationResult(6, "6-month tenure offers the lowest interest burden while keeping monthly payments manageable.")));
        when(emiRecommendationRepository.findByTransactionId("TXN100001")).thenReturn(Collections.emptyList());
        when(emiRecommendationRepository.save(any(EmiRecommendation.class))).thenAnswer(inv -> inv.getArgument(0));

        AiEmiRecommendationResponseDto response = emiRecommendationService.getEmiRecommendation("TXN100001");

        assertNotNull(response);
        assertEquals("TXN100001", response.getTransactionId());
        assertEquals(new BigDecimal("12000.00"), response.getTransactionAmount());
        assertEquals("5678", response.getCardLast4());
        assertTrue(response.isEligible());
        assertTrue(response.isAiGenerated());
        assertEquals(6, response.getRecommendedTenureMonths());
        assertEquals("6-month tenure offers the lowest interest burden while keeping monthly payments manageable.", response.getRecommendationReason());
        assertEquals(6, response.getAvailableOptions().size());
        assertEquals(new BigDecimal("180.00"), response.getProcessingFee()); // 1.5% of 12000

        verify(currentUserContext).assertCustomerOwnership("CUST1001");
        verify(emiRecommendationRepository).save(any(EmiRecommendation.class));
    }

    @Test
    void testGetEmiRecommendation_Success_FallbackWhenAiUnavailable() {
        when(transactionRepository.findById("TXN100001")).thenReturn(Optional.of(sampleTransaction));
        when(geminiApiClient.getEmiRecommendation(any(), any(), any())).thenReturn(Optional.empty());
        when(emiRecommendationRepository.findByTransactionId("TXN100001")).thenReturn(Collections.emptyList());
        when(emiRecommendationRepository.save(any(EmiRecommendation.class))).thenAnswer(inv -> inv.getArgument(0));

        AiEmiRecommendationResponseDto response = emiRecommendationService.getEmiRecommendation("TXN100001");

        assertNotNull(response);
        assertEquals("TXN100001", response.getTransactionId());
        assertTrue(response.isEligible());
        assertFalse(response.isAiGenerated());
        assertEquals(6, response.getRecommendedTenureMonths());
        assertTrue(response.getRecommendationReason().contains("6-month tenure offers a balanced monthly installment"));

        verify(currentUserContext).assertCustomerOwnership("CUST1001");
        verify(emiRecommendationRepository).save(any(EmiRecommendation.class));
    }

    @Test
    void testGetEmiRecommendation_AiReturnsInvalidTenure_FallsBackToDeterministic() {
        when(transactionRepository.findById("TXN100001")).thenReturn(Optional.of(sampleTransaction));
        // AI returns unsupported tenure (e.g. 36 months)
        when(geminiApiClient.getEmiRecommendation(any(), any(), any()))
                .thenReturn(Optional.of(new GeminiApiClient.AiRecommendationResult(36, "Use 36 months")));
        when(emiRecommendationRepository.findByTransactionId("TXN100001")).thenReturn(Collections.emptyList());
        when(emiRecommendationRepository.save(any(EmiRecommendation.class))).thenAnswer(inv -> inv.getArgument(0));

        AiEmiRecommendationResponseDto response = emiRecommendationService.getEmiRecommendation("TXN100001");

        assertNotNull(response);
        assertFalse(response.isAiGenerated());
        assertEquals(6, response.getRecommendedTenureMonths());
    }

    @Test
    void testGetEmiRecommendation_TransactionNotFound_ThrowsResourceNotFoundException() {
        when(transactionRepository.findById("TXN_UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> emiRecommendationService.getEmiRecommendation("TXN_UNKNOWN"));
    }

    @Test
    void testGetEmiRecommendation_AmountBelow3000_ThrowsIllegalArgumentException() {
        sampleTransaction.setAmount(new BigDecimal("2999.00"));
        when(transactionRepository.findById("TXN100001")).thenReturn(Optional.of(sampleTransaction));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> emiRecommendationService.getEmiRecommendation("TXN100001"));
        assertTrue(ex.getMessage().contains("at least ₹3000"));
    }

    @Test
    void testGetEmiRecommendation_CardNotActive_ThrowsIllegalArgumentException() {
        sampleCard.setCardStatus(CardStatus.BLOCKED);
        when(transactionRepository.findById("TXN100001")).thenReturn(Optional.of(sampleTransaction));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> emiRecommendationService.getEmiRecommendation("TXN100001"));
        assertTrue(ex.getMessage().contains("Card is not active"));
    }

    @Test
    void testGetEmiRecommendation_CardTypeNotEmiEligible_ThrowsIllegalArgumentException() {
        sampleCard.getCardType().setEmiEligible(false);
        when(transactionRepository.findById("TXN100001")).thenReturn(Optional.of(sampleTransaction));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> emiRecommendationService.getEmiRecommendation("TXN100001"));
        assertTrue(ex.getMessage().contains("not eligible for EMI"));
    }

    @Test
    void testGetEmiRecommendation_OwnershipCheckFails_ThrowsAccessDeniedException() {
        when(transactionRepository.findById("TXN100001")).thenReturn(Optional.of(sampleTransaction));
        doThrow(new AccessDeniedException("You are not authorized to access this resource"))
                .when(currentUserContext).assertCustomerOwnership("CUST1001");

        assertThrows(AccessDeniedException.class, () -> emiRecommendationService.getEmiRecommendation("TXN100001"));
    }

    @Test
    void testEmiCalculatorUtil_Accuracy() {
        BigDecimal principal = new BigDecimal("10000.00");
        BigDecimal annualRate = new BigDecimal("15.00");

        // 12 months EMI for 10000 at 15% p.a.
        BigDecimal emi = EmiCalculatorUtil.calculateMonthlyEmi(principal, annualRate, 12);
        assertNotNull(emi);
        assertEquals(new BigDecimal("902.58"), emi);

        // Processing fee: 1.5% of 10000 = 150.00
        BigDecimal fee = EmiCalculatorUtil.calculateProcessingFee(principal);
        assertEquals(new BigDecimal("150.00"), fee);

        // Processing fee for small transaction: 1.5% of 3000 = 45.00 -> minimum 100.00
        BigDecimal smallFee = EmiCalculatorUtil.calculateProcessingFee(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("100.00"), smallFee);
    }
}
