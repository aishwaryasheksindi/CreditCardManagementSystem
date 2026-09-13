package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiSpendingAnalysisResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Merchant;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.AiSpendingAnalysisServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.service.ISpendingInsightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiSpendingAnalysisServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ISpendingInsightService spendingInsightService;

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private AiSpendingAnalysisServiceImpl aiSpendingAnalysisService;

    private Customer sampleCustomer;
    private Card sampleCard;
    private TransactionCategory diningCat;
    private TransactionCategory travelCat;
    private TransactionCategory shoppingCat;
    private Merchant swiggy;
    private Merchant makeMyTrip;
    private Merchant amazon;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId("CUST1001");
        sampleCustomer.setName("Alice Sharma");

        sampleCard = new Card();
        sampleCard.setCardId("CARD1001");
        sampleCard.setCustomer(sampleCustomer);

        diningCat = new TransactionCategory("CAT1001", "Dining", "Restaurants");
        travelCat = new TransactionCategory("CAT1002", "Travel", "Flights & Hotels");
        shoppingCat = new TransactionCategory("CAT1003", "Shopping", "E-commerce & Retail");

        swiggy = new Merchant("MER1001", "Swiggy", "Food Delivery", "Bangalore", "contact@swiggy.in");
        makeMyTrip = new Merchant("MER1002", "MakeMyTrip", "Travel Booking", "Gurgaon", "contact@makemytrip.com");
        amazon = new Merchant("MER1003", "Amazon", "Retail", "Hyderabad", "contact@amazon.in");
    }

    @Test
    void testGetSpendingAnalysis_ExactManualCalculationsMatch() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);

        LocalDate now = LocalDate.now();
        YearMonth currentYm = YearMonth.from(now);
        LocalDateTime currentMonthMid = currentYm.atDay(10).atTime(12, 0);

        YearMonth prevYm = currentYm.minusMonths(1);
        LocalDateTime prevMonthMid = prevYm.atDay(15).atTime(14, 0);

        // Seeded current month transactions:
        // 1. Swiggy Dining: 2000.00
        // 2. MakeMyTrip Travel: 3000.00
        // 3. Amazon Shopping: 1000.00
        // 4. Swiggy Dining: 500.00
        // Total = 6500.00. Count = 4. Average = 1625.00
        // Dining = 2500.00 (38.46%)
        // Travel = 3000.00 (46.15%)
        // Shopping = 1000.00 (15.38%)
        // Top merchants: MakeMyTrip (3000), Swiggy (2500), Amazon (1000)
        List<Transaction> transactions = new ArrayList<>();

        Transaction t1 = new Transaction();
        t1.setTransactionId("TXN3001");
        t1.setCard(sampleCard);
        t1.setAmount(new BigDecimal("2000.00"));
        t1.setCategory(diningCat);
        t1.setMerchant(swiggy);
        t1.setTransactionDate(currentMonthMid);
        t1.setTransactionStatus(TransactionStatus.COMPLETED);
        transactions.add(t1);

        Transaction t2 = new Transaction();
        t2.setTransactionId("TXN3002");
        t2.setCard(sampleCard);
        t2.setAmount(new BigDecimal("3000.00"));
        t2.setCategory(travelCat);
        t2.setMerchant(makeMyTrip);
        t2.setTransactionDate(currentMonthMid.plusDays(1));
        t2.setTransactionStatus(TransactionStatus.COMPLETED);
        transactions.add(t2);

        Transaction t3 = new Transaction();
        t3.setTransactionId("TXN3003");
        t3.setCard(sampleCard);
        t3.setAmount(new BigDecimal("1000.00"));
        t3.setCategory(shoppingCat);
        t3.setMerchant(amazon);
        t3.setTransactionDate(currentMonthMid.plusDays(2));
        t3.setTransactionStatus(TransactionStatus.COMPLETED);
        transactions.add(t3);

        Transaction t4 = new Transaction();
        t4.setTransactionId("TXN3004");
        t4.setCard(sampleCard);
        t4.setAmount(new BigDecimal("500.00"));
        t4.setCategory(diningCat);
        t4.setMerchant(swiggy);
        t4.setTransactionDate(currentMonthMid.plusDays(3));
        t4.setTransactionStatus(TransactionStatus.COMPLETED);
        transactions.add(t4);

        // Previous month transaction:
        // 5. Total = 5000.00
        // MoM change = (6500 - 5000) / 5000 * 100 = +30.0%
        Transaction t5 = new Transaction();
        t5.setTransactionId("TXN3005");
        t5.setCard(sampleCard);
        t5.setAmount(new BigDecimal("5000.00"));
        t5.setCategory(shoppingCat);
        t5.setMerchant(amazon);
        t5.setTransactionDate(prevMonthMid);
        t5.setTransactionStatus(TransactionStatus.COMPLETED);
        transactions.add(t5);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId))
                .thenReturn(transactions);

        when(geminiApiClient.getSpendingSummary(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of("Spending was concentrated in Travel (46.2%) and Dining (38.5%) with a 30% monthly increase."));

        SpendingInsightResponseDto insightResponseDto = new SpendingInsightResponseDto(
                "INS100001", customerId, "Spending was concentrated in Travel (46.2%) and Dining (38.5%) with a 30% monthly increase.",
                new BigDecimal("6500.00"), currentYm.atDay(1), currentYm.atEndOfMonth()
        );
        when(spendingInsightService.createInsight(any(SpendingInsightRequestDto.class))).thenReturn(insightResponseDto);

        AiSpendingAnalysisResponseDto response = aiSpendingAnalysisService.getSpendingAnalysis(customerId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(new BigDecimal("6500.00"), response.getTotalSpending());
        assertEquals(new BigDecimal("1625.00"), response.getAverageTransactionValue());
        assertEquals(30.0, response.getMonthOverMonthChangePercent());
        assertEquals("INS100001", response.getInsightId());

        // Verify Category Breakdown exact percentages
        assertEquals(3, response.getCategoryBreakdown().size());
        assertEquals(46.15, response.getCategoryBreakdown().get("Travel"));
        assertEquals(38.46, response.getCategoryBreakdown().get("Dining"));
        assertEquals(15.38, response.getCategoryBreakdown().get("Shopping"));

        // Verify Top Merchants order
        assertEquals(List.of("MakeMyTrip", "Swiggy", "Amazon"), response.getTopMerchants());

        // Verify SpendingInsight persistence
        ArgumentCaptor<SpendingInsightRequestDto> captor = ArgumentCaptor.forClass(SpendingInsightRequestDto.class);
        verify(spendingInsightService, times(1)).createInsight(captor.capture());
        SpendingInsightRequestDto capturedInsight = captor.getValue();
        assertEquals(customerId, capturedInsight.getCustomerId());
        assertEquals(new BigDecimal("6500.00"), capturedInsight.getAmount());
        assertEquals(currentYm.atDay(1), capturedInsight.getPeriodStart());
        assertEquals(currentYm.atEndOfMonth(), capturedInsight.getPeriodEnd());
    }

    @Test
    void testGetSpendingAnalysis_GeminiFallback_UsesDeterministicSummary() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);

        LocalDate now = LocalDate.now();
        YearMonth currentYm = YearMonth.from(now);
        LocalDateTime currentMonthMid = currentYm.atDay(10).atTime(12, 0);

        Transaction t1 = new Transaction();
        t1.setTransactionId("TXN3006");
        t1.setCard(sampleCard);
        t1.setAmount(new BigDecimal("1000.00"));
        t1.setCategory(diningCat);
        t1.setMerchant(swiggy);
        t1.setTransactionDate(currentMonthMid);
        t1.setTransactionStatus(TransactionStatus.COMPLETED);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId))
                .thenReturn(List.of(t1));

        // Gemini returns empty -> fallback should trigger
        when(geminiApiClient.getSpendingSummary(any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        SpendingInsightResponseDto insightResponseDto = new SpendingInsightResponseDto(
                "INS100002", customerId, "fallback", new BigDecimal("1000.00"), currentYm.atDay(1), currentYm.atEndOfMonth()
        );
        when(spendingInsightService.createInsight(any(SpendingInsightRequestDto.class))).thenReturn(insightResponseDto);

        AiSpendingAnalysisResponseDto response = aiSpendingAnalysisService.getSpendingAnalysis(customerId);

        assertNotNull(response);
        assertTrue(response.getSummary().contains("Total spending for this month was ₹1000.00"));
        assertTrue(response.getSummary().contains("Dining"));
    }

    @Test
    void testGetSpendingAnalysis_CustomerNotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.existsById("CUST_UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> aiSpendingAnalysisService.getSpendingAnalysis("CUST_UNKNOWN"));
    }

    @Test
    void testGetSpendingAnalysis_UnauthorizedAccess_ThrowsAccessDeniedException() {
        when(customerRepository.existsById("CUST1001")).thenReturn(true);
        doThrow(new AccessDeniedException("You are not authorized to access this resource"))
                .when(currentUserContext).assertCustomerOwnership("CUST1001");

        assertThrows(AccessDeniedException.class,
                () -> aiSpendingAnalysisService.getSpendingAnalysis("CUST1001"));
    }
}
