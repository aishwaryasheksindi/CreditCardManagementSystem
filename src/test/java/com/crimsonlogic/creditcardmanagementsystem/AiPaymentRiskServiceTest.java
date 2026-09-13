package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiPaymentRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Payment;
import com.crimsonlogic.creditcardmanagementsystem.entity.Statement;
import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.PaymentRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.AiPaymentRiskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiPaymentRiskServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private AiPaymentRiskServiceImpl aiPaymentRiskService;

    private Customer cleanCustomer;
    private Customer riskyCustomer;
    private Card cleanCard;
    private Card riskyCard;

    @BeforeEach
    void setUp() {
        cleanCustomer = new Customer();
        cleanCustomer.setCustomerId("CUST_CLEAN");
        cleanCustomer.setName("Rahul Sharma");

        cleanCard = new Card();
        cleanCard.setCardId("CARD_CLEAN");
        cleanCard.setCustomer(cleanCustomer);
        cleanCard.setCreditLimit(new BigDecimal("100000.00"));

        riskyCustomer = new Customer();
        riskyCustomer.setCustomerId("CUST_RISKY");
        riskyCustomer.setName("Vijay Mallya");

        riskyCard = new Card();
        riskyCard.setCardId("CARD_RISKY");
        riskyCard.setCustomer(riskyCustomer);
        riskyCard.setCreditLimit(new BigDecimal("50000.00"));
    }

    @Test
    void testGetPaymentRisk_CleanCustomer_LowRisk() {
        String customerId = "CUST_CLEAN";
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(cardRepository.findByCustomer_CustomerId(customerId)).thenReturn(List.of(cleanCard));

        Statement stmt1 = new Statement();
        stmt1.setStatementId("STMT1");
        stmt1.setCardId("CARD_CLEAN");
        stmt1.setStatementDate(LocalDate.of(2026, 8, 1));
        stmt1.setDueDate(LocalDate.of(2026, 8, 25));
        stmt1.setClosingBalance(new BigDecimal("20000.00")); // 20.0% utilization
        stmt1.setMinimumDue(new BigDecimal("1000.00"));

        when(statementRepository.findByCardId("CARD_CLEAN")).thenReturn(List.of(stmt1));
        when(statementRepository.findTopByCardIdOrderByStatementDateDesc("CARD_CLEAN")).thenReturn(Optional.of(stmt1));

        Payment fullOnTimePayment = new Payment();
        fullOnTimePayment.setPaymentId("PAY1");
        fullOnTimePayment.setCardId("CARD_CLEAN");
        fullOnTimePayment.setAmount(new BigDecimal("20000.00")); // Full payment (not minimum only)
        fullOnTimePayment.setPaymentDate(LocalDateTime.of(2026, 8, 20, 10, 0)); // Before Aug 25 (on time)
        fullOnTimePayment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(eq("CARD_CLEAN"), any(), any(), eq(PaymentStatus.SUCCESS)))
                .thenReturn(List.of(fullOnTimePayment));

        when(geminiApiClient.getPaymentRiskExplanation(eq("LOW"), anyDouble(), eq(0), eq(0)))
                .thenReturn(Optional.of("Cardholder demonstrates excellent credit management with 20% utilization and full on-time payments."));

        AiPaymentRiskResponseDto result = aiPaymentRiskService.getPaymentRisk(customerId);

        assertNotNull(result);
        assertEquals("CUST_CLEAN", result.getCustomerId());
        assertEquals("LOW", result.getRiskLevel());
        assertEquals(20.0, result.getUtilizationPercent());
        assertEquals(0, result.getMinimumOnlyCycleCount());
        assertEquals(0, result.getLateCycleCount());
        assertEquals("Cardholder demonstrates excellent credit management with 20% utilization and full on-time payments.", result.getExplanation());
    }

    @Test
    void testGetPaymentRisk_RiskyCustomer_HighRisk() {
        String customerId = "CUST_RISKY";
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(cardRepository.findByCustomer_CustomerId(customerId)).thenReturn(List.of(riskyCard));

        // Cycle 1: Minimum payment only AND paid late
        Statement stmt1 = new Statement();
        stmt1.setStatementId("STMT2");
        stmt1.setCardId("CARD_RISKY");
        stmt1.setStatementDate(LocalDate.of(2026, 8, 1));
        stmt1.setDueDate(LocalDate.of(2026, 8, 25));
        stmt1.setClosingBalance(new BigDecimal("40000.00")); // 40000/50000 = 80.0% utilization
        stmt1.setMinimumDue(new BigDecimal("2000.00"));

        // Cycle 2: Minimum payment only, paid on time
        Statement stmt2 = new Statement();
        stmt2.setStatementId("STMT3");
        stmt2.setCardId("CARD_RISKY");
        stmt2.setStatementDate(LocalDate.of(2026, 7, 1));
        stmt2.setDueDate(LocalDate.of(2026, 7, 25));
        stmt2.setClosingBalance(new BigDecimal("38000.00"));
        stmt2.setMinimumDue(new BigDecimal("1900.00"));

        when(statementRepository.findByCardId("CARD_RISKY")).thenReturn(List.of(stmt1, stmt2));
        when(statementRepository.findTopByCardIdOrderByStatementDateDesc("CARD_RISKY")).thenReturn(Optional.of(stmt1));

        Payment minLatePayment = new Payment();
        minLatePayment.setPaymentId("PAY2");
        minLatePayment.setCardId("CARD_RISKY");
        minLatePayment.setAmount(new BigDecimal("2000.00")); // Minimum only
        minLatePayment.setPaymentDate(LocalDateTime.of(2026, 8, 28, 11, 0)); // Late! (Due Aug 25)
        minLatePayment.setPaymentStatus(PaymentStatus.SUCCESS);

        Payment minOnTimePayment = new Payment();
        minOnTimePayment.setPaymentId("PAY3");
        minOnTimePayment.setCardId("CARD_RISKY");
        minOnTimePayment.setAmount(new BigDecimal("1900.00")); // Minimum only
        minOnTimePayment.setPaymentDate(LocalDateTime.of(2026, 7, 20, 11, 0)); // On time
        minOnTimePayment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(
                eq("CARD_RISKY"), eq(stmt1.getStatementDate().atStartOfDay()), any(), eq(PaymentStatus.SUCCESS)))
                .thenReturn(List.of(minLatePayment));

        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(
                eq("CARD_RISKY"), eq(stmt2.getStatementDate().atStartOfDay()), any(), eq(PaymentStatus.SUCCESS)))
                .thenReturn(List.of(minOnTimePayment));

        when(geminiApiClient.getPaymentRiskExplanation(eq("HIGH"), anyDouble(), eq(2), eq(1)))
                .thenReturn(Optional.of("High default risk due to 80% utilization, multiple minimum-only payments, and delinquent history."));

        AiPaymentRiskResponseDto result = aiPaymentRiskService.getPaymentRisk(customerId);

        assertNotNull(result);
        assertEquals("CUST_RISKY", result.getCustomerId());
        assertEquals("HIGH", result.getRiskLevel());
        assertEquals(80.0, result.getUtilizationPercent());
        assertEquals(2, result.getMinimumOnlyCycleCount());
        assertEquals(1, result.getLateCycleCount());
        assertEquals("High default risk due to 80% utilization, multiple minimum-only payments, and delinquent history.", result.getExplanation());
    }

    @Test
    void testGetPaymentRisk_SingleRiskSignal_MediumRisk() {
        String customerId = "CUST_CLEAN";
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(cardRepository.findByCustomer_CustomerId(customerId)).thenReturn(List.of(cleanCard));

        // 1 late payment, but low utilization and full payment
        Statement stmt1 = new Statement();
        stmt1.setStatementId("STMT1");
        stmt1.setCardId("CARD_CLEAN");
        stmt1.setStatementDate(LocalDate.of(2026, 8, 1));
        stmt1.setDueDate(LocalDate.of(2026, 8, 25));
        stmt1.setClosingBalance(new BigDecimal("20000.00")); // 20%
        stmt1.setMinimumDue(new BigDecimal("1000.00"));

        when(statementRepository.findByCardId("CARD_CLEAN")).thenReturn(List.of(stmt1));
        when(statementRepository.findTopByCardIdOrderByStatementDateDesc("CARD_CLEAN")).thenReturn(Optional.of(stmt1));

        Payment lateFullPayment = new Payment();
        lateFullPayment.setPaymentId("PAY1");
        lateFullPayment.setCardId("CARD_CLEAN");
        lateFullPayment.setAmount(new BigDecimal("20000.00")); // Full payment (not min only)
        lateFullPayment.setPaymentDate(LocalDateTime.of(2026, 8, 27, 10, 0)); // Late!
        lateFullPayment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(eq("CARD_CLEAN"), any(), any(), eq(PaymentStatus.SUCCESS)))
                .thenReturn(List.of(lateFullPayment));

        when(geminiApiClient.getPaymentRiskExplanation(eq("MEDIUM"), anyDouble(), eq(0), eq(1)))
                .thenReturn(Optional.empty()); // fallback test

        AiPaymentRiskResponseDto result = aiPaymentRiskService.getPaymentRisk(customerId);

        assertNotNull(result);
        assertEquals("MEDIUM", result.getRiskLevel());
        assertEquals(20.0, result.getUtilizationPercent());
        assertEquals(0, result.getMinimumOnlyCycleCount());
        assertEquals(1, result.getLateCycleCount());
        assertTrue(result.getExplanation().contains("1 late payment cycle(s) past statement due date"));
    }

    @Test
    void testGetPaymentRisk_CustomerNotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.existsById("CUST_UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> aiPaymentRiskService.getPaymentRisk("CUST_UNKNOWN"));
    }

    @Test
    void testGetPaymentRisk_UnauthorizedAccess_ThrowsAccessDeniedException() {
        when(customerRepository.existsById("CUST_CLEAN")).thenReturn(true);
        doThrow(new AccessDeniedException("You are not authorized to access this resource"))
                .when(currentUserContext).assertCustomerOwnership("CUST_CLEAN");

        assertThrows(AccessDeniedException.class,
                () -> aiPaymentRiskService.getPaymentRisk("CUST_CLEAN"));
    }
}
