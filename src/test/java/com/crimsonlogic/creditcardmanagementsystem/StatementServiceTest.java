package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Payment;
import com.crimsonlogic.creditcardmanagementsystem.entity.Statement;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.PaymentRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.StatementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private StatementServiceImpl statementService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAddStatement_DynamicAggregation_FloorApplied() {
        String cardId = "CARD1001";
        LocalDate statementDate = LocalDate.of(2026, 9, 15);
        LocalDate dueDate = LocalDate.of(2026, 10, 5);

        StatementRequestDto requestDto = new StatementRequestDto();
        requestDto.setCardId(cardId);
        requestDto.setStatementDate(statementDate);
        requestDto.setDueDate(dueDate);
        requestDto.setOpeningBalance(new BigDecimal("1000.00"));

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(statementRepository.findTopByCardIdOrderByStatementDateDesc(cardId)).thenReturn(Optional.empty());
        when(statementRepository.existsById(any())).thenReturn(false);
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card card = new Card();
        card.setCardId(cardId);

        Transaction purchase = new Transaction();
        purchase.setCard(card);
        purchase.setAmount(new BigDecimal("1500.00"));
        purchase.setTransactionType(TransactionType.PURCHASE);
        purchase.setTransactionStatus(TransactionStatus.COMPLETED);

        Transaction withdrawal = new Transaction();
        withdrawal.setCard(card);
        withdrawal.setAmount(new BigDecimal("500.00"));
        withdrawal.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        withdrawal.setTransactionStatus(TransactionStatus.COMPLETED);

        Transaction refund = new Transaction();
        refund.setCard(card);
        refund.setAmount(new BigDecimal("200.00"));
        refund.setTransactionType(TransactionType.REFUND);
        refund.setTransactionStatus(TransactionStatus.COMPLETED);

        Transaction fee = new Transaction();
        fee.setCard(card);
        fee.setAmount(new BigDecimal("50.00"));
        fee.setTransactionType(TransactionType.FEE);
        fee.setTransactionStatus(TransactionStatus.COMPLETED);

        Transaction interest = new Transaction();
        interest.setCard(card);
        interest.setAmount(new BigDecimal("30.00"));
        interest.setTransactionType(TransactionType.INTEREST);
        interest.setTransactionStatus(TransactionStatus.COMPLETED);

        Transaction failedTxn = new Transaction();
        failedTxn.setCard(card);
        failedTxn.setAmount(new BigDecimal("1000.00"));
        failedTxn.setTransactionType(TransactionType.PURCHASE);
        failedTxn.setTransactionStatus(TransactionStatus.FAILED);

        when(transactionRepository.findByCard_CardIdAndTransactionDateBetween(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(purchase, withdrawal, refund, fee, interest, failedTxn));

        Payment payment = new Payment();
        payment.setCardId(cardId);
        payment.setAmount(new BigDecimal("800.00"));
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class), eq(PaymentStatus.SUCCESS)))
                .thenReturn(Collections.singletonList(payment));

        StatementResponseDto result = statementService.addStatement(requestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getOpeningBalance());
        assertEquals(new BigDecimal("2000.00"), result.getTotalPurchases()); // 1500 + 500 (failed ignored)
        assertEquals(new BigDecimal("800.00"), result.getTotalPayments());
        assertEquals(new BigDecimal("200.00"), result.getTotalRefunds());
        assertEquals(new BigDecimal("50.00"), result.getTotalFees());
        assertEquals(new BigDecimal("30.00"), result.getTotalInterest());
        // Closing balance: 1000 + 2000 - 800 - 200 + 50 + 30 = 2080.00
        assertEquals(new BigDecimal("2080.00"), result.getClosingBalance());
        // 5% of 2080 is 104.00 -> floor of 200.00 applies
        assertEquals(new BigDecimal("200.00"), result.getMinimumDue());
    }

    @Test
    void testAddStatement_RolloverOpeningBalance_AboveFloorMinDue() {
        String cardId = "CARD1001";
        LocalDate statementDate = LocalDate.of(2026, 9, 15);
        LocalDate dueDate = LocalDate.of(2026, 10, 5);

        StatementRequestDto requestDto = new StatementRequestDto();
        requestDto.setCardId(cardId);
        requestDto.setStatementDate(statementDate);
        requestDto.setDueDate(dueDate);

        Statement priorStatement = new Statement();
        priorStatement.setStatementId("STMT1001");
        priorStatement.setCardId(cardId);
        priorStatement.setStatementDate(LocalDate.of(2026, 8, 15));
        priorStatement.setClosingBalance(new BigDecimal("5500.00"));

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(statementRepository.findTopByCardIdOrderByStatementDateDesc(cardId)).thenReturn(Optional.of(priorStatement));
        when(statementRepository.existsById(any())).thenReturn(false);
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card card = new Card();
        card.setCardId(cardId);

        Transaction purchase = new Transaction();
        purchase.setCard(card);
        purchase.setAmount(new BigDecimal("10000.00"));
        purchase.setTransactionType(TransactionType.PURCHASE);
        purchase.setTransactionStatus(TransactionStatus.COMPLETED);

        when(transactionRepository.findByCard_CardIdAndTransactionDateBetween(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(purchase));

        Payment payment = new Payment();
        payment.setCardId(cardId);
        payment.setAmount(new BigDecimal("2000.00"));
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class), eq(PaymentStatus.SUCCESS)))
                .thenReturn(Collections.singletonList(payment));

        StatementResponseDto result = statementService.addStatement(requestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("5500.00"), result.getOpeningBalance()); // from prior statement
        assertEquals(new BigDecimal("10000.00"), result.getTotalPurchases());
        assertEquals(new BigDecimal("2000.00"), result.getTotalPayments());
        // Closing: 5500 + 10000 - 2000 = 13500.00
        assertEquals(new BigDecimal("13500.00"), result.getClosingBalance());
        // 5% of 13500 is 675.00 (> 200.00)
        assertEquals(new BigDecimal("675.00"), result.getMinimumDue());
    }

    @Test
    void testAddStatement_ZeroBalance_MinimumDueZero() {
        String cardId = "CARD1001";
        StatementRequestDto requestDto = new StatementRequestDto();
        requestDto.setCardId(cardId);
        requestDto.setStatementDate(LocalDate.now());
        requestDto.setDueDate(LocalDate.now().plusDays(20));

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(statementRepository.findTopByCardIdOrderByStatementDateDesc(cardId)).thenReturn(Optional.empty());
        when(statementRepository.existsById(any())).thenReturn(false);
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.findByCard_CardIdAndTransactionDateBetween(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class), eq(PaymentStatus.SUCCESS)))
                .thenReturn(Collections.emptyList());

        StatementResponseDto result = statementService.addStatement(requestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("0.00"), result.getClosingBalance());
        assertEquals(new BigDecimal("0.00"), result.getMinimumDue());
    }

    @Test
    void testAddStatement_OverpaymentNegativeBalance_MinimumDueZero() {
        String cardId = "CARD1001";
        StatementRequestDto requestDto = new StatementRequestDto();
        requestDto.setCardId(cardId);
        requestDto.setStatementDate(LocalDate.now());
        requestDto.setDueDate(LocalDate.now().plusDays(20));
        requestDto.setOpeningBalance(new BigDecimal("500.00"));

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(statementRepository.findTopByCardIdOrderByStatementDateDesc(cardId)).thenReturn(Optional.empty());
        when(statementRepository.existsById(any())).thenReturn(false);
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.findByCard_CardIdAndTransactionDateBetween(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Payment payment = new Payment();
        payment.setCardId(cardId);
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(eq(cardId), any(LocalDateTime.class), any(LocalDateTime.class), eq(PaymentStatus.SUCCESS)))
                .thenReturn(Collections.singletonList(payment));

        StatementResponseDto result = statementService.addStatement(requestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("-500.00"), result.getClosingBalance());
        assertEquals(new BigDecimal("0.00"), result.getMinimumDue());
    }

    @Test
    void testAddStatement_CardNotFound_ThrowsResourceNotFoundException() {
        StatementRequestDto requestDto = new StatementRequestDto();
        requestDto.setCardId("CARD_NON_EXISTENT");
        requestDto.setStatementDate(LocalDate.now());
        requestDto.setDueDate(LocalDate.now().plusDays(20));

        when(cardRepository.existsById("CARD_NON_EXISTENT")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> statementService.addStatement(requestDto));
        verify(statementRepository, never()).save(any());
    }
}
