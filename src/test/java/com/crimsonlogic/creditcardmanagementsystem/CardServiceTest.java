package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.VerificationLockedException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardTypeRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.CardServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardTypeRepository cardTypeRepository;

    @Mock
    private IAuditLogService auditLogService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void testAddCard_AvailableLimitExceedsCreditLimit_ThrowsException() {
        CardRequestDto cardDto = new CardRequestDto();
        cardDto.setCreditLimit(new BigDecimal("50000.00"));
        cardDto.setAvailableLimit(new BigDecimal("60000.00"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cardService.addCard(cardDto);
        });

        assertEquals("Available limit cannot exceed credit limit", ex.getMessage());
    }

    @Test
    void testAddCard_ValidLimits_Success() {
        CardRequestDto cardDto = new CardRequestDto();
        cardDto.setCustomerId("CUST1001");
        cardDto.setCardTypeId("CT1001");
        cardDto.setCreditLimit(new BigDecimal("50000.00"));
        cardDto.setAvailableLimit(new BigDecimal("50000.00"));
        cardDto.setCardStatus(CardStatus.ACTIVE);
        cardDto.setCardReference("CARD-REF-123");
        cardDto.setBillingCycle("1st of month");
        cardDto.setInterestRate(new BigDecimal("15.0"));
        cardDto.setAnnualFee(BigDecimal.ZERO);
        cardDto.setExpiryDate(LocalDate.now().plusYears(3));
        cardDto.setIssuanceDate(LocalDate.now());

        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setKycStatus(KycStatus.VERIFIED);

        CardType cardType = new CardType();
        cardType.setCardTypeId("CT1001");

        when(cardRepository.existsById(any())).thenReturn(false);
        when(customerRepository.findById("CUST1001")).thenReturn(Optional.of(customer));
        when(cardTypeRepository.findById("CT1001")).thenReturn(Optional.of(cardType));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardResponseDto result = cardService.addCard(cardDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("50000.00"), result.getCreditLimit());
        assertEquals(new BigDecimal("50000.00"), result.getAvailableLimit());
    }

    @Test
    void testAddCard_CustomerKycNotVerified_ThrowsVerificationLockedException() {
        CardRequestDto cardDto = new CardRequestDto();
        cardDto.setCustomerId("CUST1001");
        cardDto.setCreditLimit(new BigDecimal("50000.00"));
        cardDto.setAvailableLimit(new BigDecimal("50000.00"));

        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setKycStatus(KycStatus.PENDING);

        when(cardRepository.existsById(any())).thenReturn(false);
        when(customerRepository.findById("CUST1001")).thenReturn(Optional.of(customer));

        VerificationLockedException ex = assertThrows(VerificationLockedException.class, () -> {
            cardService.addCard(cardDto);
        });

        assertTrue(ex.getMessage().contains("Cards can only be issued to VERIFIED customers"));
    }

    @Test
    void testUpdateCard_AvailableLimitExceedsCreditLimit_ThrowsException() {
        CardRequestDto cardDto = new CardRequestDto();
        cardDto.setCreditLimit(new BigDecimal("50000.00"));
        cardDto.setAvailableLimit(new BigDecimal("70000.00"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cardService.updateCard("CARD1001", cardDto);
        });

        assertEquals("Available limit cannot exceed credit limit", ex.getMessage());
    }

    @Test
    void testUpdateCard_StatusChange_LogsAuditAction() {
        String cardId = "CARD1001";
        Card existingCard = new Card();
        existingCard.setCardId(cardId);
        existingCard.setCardStatus(CardStatus.ACTIVE);
        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        existingCard.setCustomer(customer);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardRequestDto updateDto = new CardRequestDto();
        updateDto.setCardStatus(CardStatus.BLOCKED);

        CardResponseDto response = cardService.updateCard(cardId, updateDto);

        assertNotNull(response);
        assertEquals(CardStatus.BLOCKED, response.getCardStatus());
        verify(auditLogService, times(1)).logAction(
                eq("CUST1001"),
                eq(com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction.STATUS_CHANGE),
                eq("Card"),
                eq(cardId),
                contains("Card status changed from ACTIVE to BLOCKED")
        );
    }

    @Test
    void testSetPin_Success() {
        String cardId = "CARD1001";
        Card existingCard = new Card();
        existingCard.setCardId(cardId);
        existingCard.setFailedPinAttempts(2);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cardService.setPin(cardId, "1234");

        assertEquals("encoded1234", existingCard.getPinHash());
        assertNotNull(existingCard.getPinSetAt());
        assertEquals(0, existingCard.getFailedPinAttempts());
        verify(cardRepository, times(1)).save(existingCard);
    }

    @Test
    void testVerifyPin_PinNotSet_ThrowsException() {
        String cardId = "CARD1001";
        Card existingCard = new Card();
        existingCard.setCardId(cardId);
        existingCard.setPinHash(null);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cardService.verifyPin(cardId, "1234");
        });

        assertEquals("PIN not set for this card", ex.getMessage());
    }

    @Test
    void testVerifyPin_Success_ResetsFailedAttempts() {
        String cardId = "CARD1001";
        Card existingCard = new Card();
        existingCard.setCardId(cardId);
        existingCard.setPinHash("encoded1234");
        existingCard.setFailedPinAttempts(2);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(passwordEncoder.matches("1234", "encoded1234")).thenReturn(true);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = cardService.verifyPin(cardId, "1234");

        assertTrue(result);
        assertEquals(0, existingCard.getFailedPinAttempts());
        verify(cardRepository, times(1)).save(existingCard);
    }

    @Test
    void testVerifyPin_WrongPinUnder3_ThrowsIncorrectPin() {
        String cardId = "CARD1001";
        Card existingCard = new Card();
        existingCard.setCardId(cardId);
        existingCard.setPinHash("encoded1234");
        existingCard.setFailedPinAttempts(0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(passwordEncoder.matches("9999", "encoded1234")).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cardService.verifyPin(cardId, "9999");
        });

        assertEquals("Incorrect PIN", ex.getMessage());
        assertEquals(1, existingCard.getFailedPinAttempts());
        verify(cardRepository, times(1)).save(existingCard);
    }

    @Test
    void testVerifyPin_WrongPinReaches3_BlocksCardAndLogsAudit() {
        String cardId = "CARD1001";
        Card existingCard = new Card();
        existingCard.setCardId(cardId);
        existingCard.setPinHash("encoded1234");
        existingCard.setFailedPinAttempts(2);
        existingCard.setCardStatus(CardStatus.ACTIVE);
        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        existingCard.setCustomer(customer);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(passwordEncoder.matches("9999", "encoded1234")).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cardService.verifyPin(cardId, "9999");
        });

        assertEquals("Card blocked due to repeated incorrect PIN attempts", ex.getMessage());
        assertEquals(3, existingCard.getFailedPinAttempts());
        assertEquals(CardStatus.BLOCKED, existingCard.getCardStatus());
        verify(cardRepository, times(1)).save(existingCard);
        verify(auditLogService, times(1)).logAction(
                eq("CUST1001"),
                eq(com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction.STATUS_CHANGE),
                eq("Card"),
                eq(cardId),
                eq("Card auto-blocked after 3 failed PIN attempts")
        );
    }
}
