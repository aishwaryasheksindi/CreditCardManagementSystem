package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.AnomalyDetectionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Merchant;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.enums.AnomalyType;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.AnomalyDetectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalyDetectionServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private AnomalyDetectionServiceImpl anomalyDetectionService;

    private Customer testCustomer;
    private Card testCard;
    private TransactionCategory diningCategory;
    private TransactionCategory travelCategory;
    private TransactionCategory electronicsCategory;
    private Merchant starbucksMerchant;
    private Merchant luxuryJewelerMerchant;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId("CUST_001");

        testCard = new Card();
        testCard.setCardId("CARD_001");
        testCard.setCardReference("411111******1111");
        testCard.setCustomer(testCustomer);
        testCard.setCardStatus(CardStatus.ACTIVE);
        testCard.setFailedPinAttempts(0);

        diningCategory = new TransactionCategory("CAT_DINING", "Dining", "Restaurants and food");
        travelCategory = new TransactionCategory("CAT_TRAVEL", "Travel", "Flights and hotels");
        electronicsCategory = new TransactionCategory("CAT_ELEC", "Electronics", "Gadgets and tech");

        starbucksMerchant = new Merchant("MERCH_01", "Starbucks", "Dining", "Bangalore", "info@starbucks.com");
        luxuryJewelerMerchant = new Merchant("MERCH_02", "Luxury Jewelers", "Jewelry", "Dubai", "info@jewel.com");
    }

    @Test
    void testDetectAnomalies_CustomerNotFound_ThrowsException() {
        when(customerRepository.existsById("CUST_NONEXISTENT")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                anomalyDetectionService.detectAnomalies("CUST_NONEXISTENT"));

        verify(customerRepository, times(1)).existsById("CUST_NONEXISTENT");
        verifyNoInteractions(transactionRepository, cardRepository);
    }

    @Test
    void testDetectAnomalies_CleanCustomer_NoAnomalies() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");
        when(cardRepository.findByCustomer_CustomerId("CUST_001")).thenReturn(List.of(testCard));

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        // 5 Historical baseline transactions (20 days ago, Dining, ₹500, Bangalore, 2 PM)
        for (int i = 0; i < 5; i++) {
            Transaction h = new Transaction("HIST_" + i, testCard, starbucksMerchant, diningCategory,
                    new BigDecimal("500.00"), "INR", now.minusDays(20 + i).withHour(14), "Bangalore", TransactionStatus.COMPLETED);
            transactions.add(h);
        }

        // 1 Recent transaction matching historical baseline (2 days ago, Dining, ₹550, Bangalore, 2 PM)
        Transaction recent = new Transaction("REC_01", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("550.00"), "INR", now.minusDays(2).withHour(14), "Bangalore", TransactionStatus.COMPLETED);
        transactions.add(recent);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(transactions);

        AnomalyDetectionResponseDto result = anomalyDetectionService.detectAnomalies("CUST_001");

        assertNotNull(result);
        assertEquals("CUST_001", result.getCustomerId());
        assertTrue(result.getFlags().isEmpty(), "Clean customer should have zero anomaly flags");
    }

    @Test
    void testDetectAnomalies_SpendingSpike_Flagged() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");
        when(cardRepository.findByCustomer_CustomerId("CUST_001")).thenReturn(List.of(testCard));

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        // Historical baseline in Dining: average ₹500
        for (int i = 0; i < 5; i++) {
            Transaction h = new Transaction("HIST_" + i, testCard, starbucksMerchant, diningCategory,
                    new BigDecimal("500.00"), "INR", now.minusDays(15 + i).withHour(12), "Bangalore", TransactionStatus.COMPLETED);
            transactions.add(h);
        }

        // Recent transaction: ₹3,000 (6.0x average) -> SPENDING_SPIKE (HIGH)
        Transaction spike = new Transaction("REC_SPIKE", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("3000.00"), "INR", now.minusDays(1).withHour(12), "Bangalore", TransactionStatus.COMPLETED);
        transactions.add(spike);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(transactions);

        AnomalyDetectionResponseDto result = anomalyDetectionService.detectAnomalies("CUST_001");

        assertNotNull(result);
        boolean foundSpike = result.getFlags().stream()
                .anyMatch(f -> f.getType() == AnomalyType.SPENDING_SPIKE && "HIGH".equals(f.getSeverity()));
        assertTrue(foundSpike, "Should detect HIGH severity SPENDING_SPIKE");
    }

    @Test
    void testDetectAnomalies_UnusualMerchantAndLocation_Flagged() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");
        when(cardRepository.findByCustomer_CustomerId("CUST_001")).thenReturn(List.of(testCard));

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        // Historical transactions: Bangalore, Dining
        for (int i = 0; i < 5; i++) {
            Transaction h = new Transaction("HIST_" + i, testCard, starbucksMerchant, diningCategory,
                    new BigDecimal("500.00"), "INR", now.minusDays(20 + i).withHour(13), "Bangalore", TransactionStatus.COMPLETED);
            transactions.add(h);
        }

        // Recent transaction at Luxury Jewelers in Dubai
        Transaction unusual = new Transaction("REC_UNUSUAL", testCard, luxuryJewelerMerchant, diningCategory,
                new BigDecimal("500.00"), "INR", now.minusDays(2).withHour(13), "Dubai", TransactionStatus.COMPLETED);
        transactions.add(unusual);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(transactions);

        AnomalyDetectionResponseDto result = anomalyDetectionService.detectAnomalies("CUST_001");

        assertNotNull(result);
        boolean foundMerchant = result.getFlags().stream()
                .anyMatch(f -> f.getType() == AnomalyType.UNUSUAL_MERCHANT);
        boolean foundLocation = result.getFlags().stream()
                .anyMatch(f -> f.getType() == AnomalyType.UNUSUAL_LOCATION);

        assertTrue(foundMerchant, "Should flag UNUSUAL_MERCHANT for unobserved Jewelry merchant category");
        assertTrue(foundLocation, "Should flag UNUSUAL_LOCATION for Dubai");
    }

    @Test
    void testDetectAnomalies_UnusualTime_Flagged() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");
        when(cardRepository.findByCustomer_CustomerId("CUST_001")).thenReturn(List.of(testCard));

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        // Historical: Daytime only (2 PM)
        for (int i = 0; i < 5; i++) {
            Transaction h = new Transaction("HIST_" + i, testCard, starbucksMerchant, diningCategory,
                    new BigDecimal("500.00"), "INR", now.minusDays(15 + i).withHour(14), "Bangalore", TransactionStatus.COMPLETED);
            transactions.add(h);
        }

        // Recent transaction at 03:30 AM
        Transaction lateNight = new Transaction("REC_NIGHT", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("500.00"), "INR", now.minusDays(1).withHour(3).withMinute(30), "Bangalore", TransactionStatus.COMPLETED);
        transactions.add(lateNight);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(transactions);

        AnomalyDetectionResponseDto result = anomalyDetectionService.detectAnomalies("CUST_001");

        assertNotNull(result);
        boolean foundTime = result.getFlags().stream()
                .anyMatch(f -> f.getType() == AnomalyType.UNUSUAL_TIME);
        assertTrue(foundTime, "Should flag UNUSUAL_TIME for 3:30 AM transaction");
    }

    @Test
    void testDetectAnomalies_RapidTransactions_Flagged() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");
        when(cardRepository.findByCustomer_CustomerId("CUST_001")).thenReturn(List.of(testCard));

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        // Historical baseline
        for (int i = 0; i < 5; i++) {
            Transaction h = new Transaction("HIST_" + i, testCard, starbucksMerchant, diningCategory,
                    new BigDecimal("500.00"), "INR", now.minusDays(20 + i).withHour(12), "Bangalore", TransactionStatus.COMPLETED);
            transactions.add(h);
        }

        // Recent 3 rapid transactions within 10 minutes
        LocalDateTime burstTime = now.minusDays(1).withHour(15).withMinute(0);
        Transaction r1 = new Transaction("REC_RAPID_1", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("500.00"), "INR", burstTime, "Bangalore", TransactionStatus.COMPLETED);
        Transaction r2 = new Transaction("REC_RAPID_2", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("500.00"), "INR", burstTime.plusMinutes(2), "Bangalore", TransactionStatus.COMPLETED);
        Transaction r3 = new Transaction("REC_RAPID_3", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("500.00"), "INR", burstTime.plusMinutes(5), "Bangalore", TransactionStatus.COMPLETED);

        transactions.add(r1);
        transactions.add(r2);
        transactions.add(r3);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(transactions);

        AnomalyDetectionResponseDto result = anomalyDetectionService.detectAnomalies("CUST_001");

        assertNotNull(result);
        boolean foundRapid = result.getFlags().stream()
                .anyMatch(f -> f.getType() == AnomalyType.RAPID_TRANSACTIONS);
        assertTrue(foundRapid, "Should flag RAPID_TRANSACTIONS for 3 transactions in 5 minutes");
    }

    @Test
    void testDetectAnomalies_NewCategory_Flagged() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");
        when(cardRepository.findByCustomer_CustomerId("CUST_001")).thenReturn(List.of(testCard));

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        // Historical transactions in Dining only
        for (int i = 0; i < 5; i++) {
            Transaction h = new Transaction("HIST_" + i, testCard, starbucksMerchant, diningCategory,
                    new BigDecimal("500.00"), "INR", now.minusDays(15 + i).withHour(12), "Bangalore", TransactionStatus.COMPLETED);
            transactions.add(h);
        }

        // Recent transaction in Electronics
        Transaction newCatTxn = new Transaction("REC_NEW_CAT", testCard, starbucksMerchant, electronicsCategory,
                new BigDecimal("500.00"), "INR", now.minusDays(2).withHour(12), "Bangalore", TransactionStatus.COMPLETED);
        transactions.add(newCatTxn);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(transactions);

        AnomalyDetectionResponseDto result = anomalyDetectionService.detectAnomalies("CUST_001");

        assertNotNull(result);
        boolean foundNewCat = result.getFlags().stream()
                .anyMatch(f -> f.getType() == AnomalyType.NEW_CATEGORY);
        assertTrue(foundNewCat, "Should flag NEW_CATEGORY for first-ever Electronics purchase");
    }

    @Test
    void testDetectAnomalies_RepeatedFailedPinAttempts_Flagged() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        // Card with 3 failed PIN attempts
        testCard.setFailedPinAttempts(3);
        when(cardRepository.findByCustomer_CustomerId("CUST_001")).thenReturn(List.of(testCard));
        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(List.of());

        AnomalyDetectionResponseDto result = anomalyDetectionService.detectAnomalies("CUST_001");

        assertNotNull(result);
        boolean foundPin = result.getFlags().stream()
                .anyMatch(f -> f.getType() == AnomalyType.REPEATED_FAILED_ATTEMPTS && "HIGH".equals(f.getSeverity()));
        assertTrue(foundPin, "Should flag HIGH severity REPEATED_FAILED_ATTEMPTS for 3 failed attempts");
    }
}
