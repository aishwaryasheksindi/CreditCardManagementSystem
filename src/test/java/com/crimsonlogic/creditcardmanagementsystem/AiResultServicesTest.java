package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;
import com.crimsonlogic.creditcardmanagementsystem.entity.*;
import com.crimsonlogic.creditcardmanagementsystem.repository.*;
import com.crimsonlogic.creditcardmanagementsystem.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiResultServicesTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SpendingInsightRepository spendingInsightRepository;

    @Mock
    private CreditRecommendationRepository creditRecommendationRepository;

    @Mock
    private EmiRecommendationRepository emiRecommendationRepository;

    @Mock
    private RewardRecommendationRepository rewardRecommendationRepository;

    @Mock
    private ChatHistoryRepository chatHistoryRepository;

    @InjectMocks
    private SpendingInsightServiceImpl spendingInsightService;

    @InjectMocks
    private CreditRecommendationServiceImpl creditRecommendationService;

    @InjectMocks
    private EmiRecommendationServiceImpl emiRecommendationService;

    @InjectMocks
    private RewardRecommendationServiceImpl rewardRecommendationService;

    @InjectMocks
    private ChatHistoryServiceImpl chatHistoryService;

    @Test
    void testSpendingInsightService_Create() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(spendingInsightRepository.existsById(any())).thenReturn(false);
        when(spendingInsightRepository.save(any(SpendingInsight.class))).thenAnswer(inv -> inv.getArgument(0));

        SpendingInsightRequestDto requestDto = new SpendingInsightRequestDto(
                customerId, "Dining spending increased by 25%", new BigDecimal("4500.00"),
                LocalDate.now().minusDays(30), LocalDate.now()
        );

        SpendingInsightResponseDto result = spendingInsightService.createInsight(requestDto);

        assertNotNull(result);
        assertTrue(result.getInsightId().startsWith("INS"));
        assertEquals(customerId, result.getCustomerId());
        assertEquals("Dining spending increased by 25%", result.getObservation());
    }

    @Test
    void testCreditRecommendationService_Create() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(creditRecommendationRepository.existsById(any())).thenReturn(false);
        when(creditRecommendationRepository.save(any(CreditRecommendation.class))).thenAnswer(inv -> inv.getArgument(0));

        CreditRecommendationRequestDto requestDto = new CreditRecommendationRequestDto(
                customerId, new BigDecimal("50000.00"), new BigDecimal("75000.00"), new BigDecimal("100000.00"), "Consistent on-time repayments"
        );

        CreditRecommendationResponseDto result = creditRecommendationService.createRecommendation(requestDto);

        assertNotNull(result);
        assertTrue(result.getRecommendationId().startsWith("CR"));
        assertEquals(customerId, result.getCustomerId());
        assertEquals(new BigDecimal("75000.00"), result.getRecommendedMin());
    }

    @Test
    void testEmiRecommendationService_Create() {
        String txnId = "TXN100001";
        when(transactionRepository.existsById(txnId)).thenReturn(true);
        when(emiRecommendationRepository.existsById(any())).thenReturn(false);
        when(emiRecommendationRepository.save(any(EmiRecommendation.class))).thenAnswer(inv -> inv.getArgument(0));

        EmiRecommendationRequestDto requestDto = new EmiRecommendationRequestDto(
                txnId, 6, new BigDecimal("12600.00"), new BigDecimal("199.00")
        );

        EmiRecommendationResponseDto result = emiRecommendationService.createRecommendation(requestDto);

        assertNotNull(result);
        assertTrue(result.getRecommendationId().startsWith("ER"));
        assertEquals(txnId, result.getTransactionId());
        assertEquals(6, result.getTenureMonths());
    }

    @Test
    void testRewardRecommendationService_Create() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(rewardRecommendationRepository.existsById(any())).thenReturn(false);
        when(rewardRecommendationRepository.save(any(RewardRecommendation.class))).thenAnswer(inv -> inv.getArgument(0));

        RewardRecommendationRequestDto requestDto = new RewardRecommendationRequestDto(
                customerId, "5x Points on Weekend Fuel", "High weekend fuel transaction frequency"
        );

        RewardRecommendationResponseDto result = rewardRecommendationService.createRecommendation(requestDto);

        assertNotNull(result);
        assertTrue(result.getRecommendationId().startsWith("RR"));
        assertEquals(customerId, result.getCustomerId());
        assertEquals("5x Points on Weekend Fuel", result.getOfferName());
    }

    @Test
    void testChatHistoryService_Create() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(chatHistoryRepository.existsById(any())).thenReturn(false);
        when(chatHistoryRepository.save(any(ChatHistory.class))).thenAnswer(inv -> inv.getArgument(0));

        ChatHistoryRequestDto requestDto = new ChatHistoryRequestDto(
                customerId, "How do I increase my limit?", "You can apply for limit increase via app.", null
        );

        ChatHistoryResponseDto result = chatHistoryService.createChat(requestDto);

        assertNotNull(result);
        assertTrue(result.getChatId().startsWith("CHT"));
        assertEquals(customerId, result.getCustomerId());
        assertNotNull(result.getAskedAt());
    }
}
