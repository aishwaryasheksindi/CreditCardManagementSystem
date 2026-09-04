package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Merchant;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.MerchantRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionCategoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardService;
import com.crimsonlogic.creditcardmanagementsystem.service.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private TransactionCategoryRepository categoryRepository;

    @Mock
    private ICardService cardService;

    @Mock
    private com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext currentUserContext;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequestDto createSampleDto(TransactionType type, String pin) {
        TransactionRequestDto dto = new TransactionRequestDto();
        dto.setCardId("CARD1001");
        dto.setMerchantId("MERCH1001");
        dto.setCategoryId("CAT1001");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setCurrency("USD");
        dto.setTransactionDate(LocalDateTime.now());
        dto.setTransactionLocation("ATM Downtown");
        dto.setTransactionStatus(TransactionStatus.COMPLETED);
        dto.setTransactionType(type);
        dto.setPin(pin);
        return dto;
    }

    @Test
    void testAddTransaction_CardNotActive_ThrowsBusinessRule1Exception() {
        TransactionRequestDto dto = createSampleDto(TransactionType.PURCHASE, null);

        Card blockedCard = new Card();
        blockedCard.setCardId("CARD1001");
        blockedCard.setCardStatus(CardStatus.BLOCKED);

        when(transactionRepository.existsById(any())).thenReturn(false);
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(blockedCard));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dto);
        });

        assertEquals("Card is BLOCKED — transactions are not allowed on this card", ex.getMessage());
    }

    @Test
    void testAddTransaction_CashWithdrawalWithoutPin_ThrowsException() {
        TransactionRequestDto dto = createSampleDto(TransactionType.CASH_WITHDRAWAL, null);

        Card activeCard = new Card();
        activeCard.setCardId("CARD1001");
        activeCard.setCardStatus(CardStatus.ACTIVE);

        when(transactionRepository.existsById(any())).thenReturn(false);
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(activeCard));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dto);
        });

        assertEquals("PIN is required for this transaction type", ex.getMessage());
        verify(cardService, never()).verifyPin(any(), any());
    }

    @Test
    void testAddTransaction_CashWithdrawalWithCorrectPin_Success() {
        TransactionRequestDto dto = createSampleDto(TransactionType.CASH_WITHDRAWAL, "1234");

        Card activeCard = new Card();
        activeCard.setCardId("CARD1001");
        activeCard.setCardStatus(CardStatus.ACTIVE);
        activeCard.setCreditLimit(new BigDecimal("50000.00"));
        activeCard.setAvailableLimit(new BigDecimal("50000.00"));

        Merchant merchant = new Merchant();
        merchant.setMerchantId("MERCH1001");

        TransactionCategory category = new TransactionCategory();
        category.setCategoryId("CAT1001");

        when(transactionRepository.existsById(any())).thenReturn(false);
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(activeCard));
        when(merchantRepository.findById("MERCH1001")).thenReturn(Optional.of(merchant));
        when(categoryRepository.findById("CAT1001")).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(cardService.verifyPin("CARD1001", "1234")).thenReturn(true);

        TransactionResponseDto response = transactionService.addTransaction(dto);

        assertNotNull(response);
        assertEquals(TransactionType.CASH_WITHDRAWAL, response.getTransactionType());
        verify(cardService, times(1)).verifyPin("CARD1001", "1234");
    }

    @Test
    void testAddTransaction_PurchaseWithoutPin_Success() {
        TransactionRequestDto dto = createSampleDto(TransactionType.PURCHASE, null);

        Card activeCard = new Card();
        activeCard.setCardId("CARD1001");
        activeCard.setCardStatus(CardStatus.ACTIVE);
        activeCard.setCreditLimit(new BigDecimal("50000.00"));
        activeCard.setAvailableLimit(new BigDecimal("50000.00"));

        Merchant merchant = new Merchant();
        merchant.setMerchantId("MERCH1001");

        TransactionCategory category = new TransactionCategory();
        category.setCategoryId("CAT1001");

        when(transactionRepository.existsById(any())).thenReturn(false);
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(activeCard));
        when(merchantRepository.findById("MERCH1001")).thenReturn(Optional.of(merchant));
        when(categoryRepository.findById("CAT1001")).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto response = transactionService.addTransaction(dto);

        assertNotNull(response);
        assertEquals(TransactionType.PURCHASE, response.getTransactionType());
        verify(cardService, never()).verifyPin(any(), any());
    }

    @Test
    void testAddTransaction_CashWithdrawalWrongPinThreeTimes_CardAutoBlockedAndFutureTransactionsFail() {
        TransactionRequestDto dto = createSampleDto(TransactionType.CASH_WITHDRAWAL, "9999");

        Card card = new Card();
        card.setCardId("CARD1001");
        card.setCardStatus(CardStatus.ACTIVE);

        when(transactionRepository.existsById(any())).thenReturn(false);
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(card));

        // When verifyPin fails on 3rd attempt, cardService throws IllegalArgumentException and sets card to BLOCKED
        doAnswer(invocation -> {
            card.setCardStatus(CardStatus.BLOCKED);
            throw new IllegalArgumentException("Card blocked due to repeated incorrect PIN attempts");
        }).when(cardService).verifyPin("CARD1001", "9999");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dto);
        });
        assertEquals("Card blocked due to repeated incorrect PIN attempts", ex.getMessage());

        // Now attempt any transaction on this card - should be rejected by Business Rule #1!
        IllegalArgumentException subsequentEx = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dto);
        });
        assertEquals("Card is BLOCKED — transactions are not allowed on this card", subsequentEx.getMessage());
    }

    @Test
    void testAvailableLimitEnforcementAndRefund_Rule2AndRule5() {
        // A card with ₹5,000 available limit
        Card card = new Card();
        card.setCardId("CARD1001");
        card.setCardStatus(CardStatus.ACTIVE);
        card.setCreditLimit(new BigDecimal("10000.00"));
        card.setAvailableLimit(new BigDecimal("5000.00"));

        Merchant merchant = new Merchant();
        merchant.setMerchantId("MERCH1001");
        TransactionCategory category = new TransactionCategory();
        category.setCategoryId("CAT1001");

        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(card));
        when(merchantRepository.findById("MERCH1001")).thenReturn(Optional.of(merchant));
        when(categoryRepository.findById("CAT1001")).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // a ₹4,000 PURCHASE succeeds and available drops to ₹1,000
        TransactionRequestDto purchase1 = createSampleDto(TransactionType.PURCHASE, null);
        purchase1.setAmount(new BigDecimal("4000.00"));
        transactionService.addTransaction(purchase1);
        assertEquals(new BigDecimal("1000.00"), card.getAvailableLimit());

        // a further ₹2,000 PURCHASE is rejected
        TransactionRequestDto purchase2 = createSampleDto(TransactionType.PURCHASE, null);
        purchase2.setAmount(new BigDecimal("2000.00"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(purchase2);
        });
        assertEquals("Transaction amount exceeds available credit limit", ex.getMessage());
        assertEquals(new BigDecimal("1000.00"), card.getAvailableLimit()); // unchanged

        // a ₹1,000 REFUND on that card brings available back to ₹2,000
        TransactionRequestDto refund = createSampleDto(TransactionType.REFUND, null);
        refund.setAmount(new BigDecimal("1000.00"));
        transactionService.addTransaction(refund);
        assertEquals(new BigDecimal("2000.00"), card.getAvailableLimit());
    }
}
