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
}
