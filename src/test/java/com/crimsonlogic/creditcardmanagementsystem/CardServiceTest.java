package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardTypeRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.CardServiceImpl;
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
    void testUpdateCard_AvailableLimitExceedsCreditLimit_ThrowsException() {
        CardRequestDto cardDto = new CardRequestDto();
        cardDto.setCreditLimit(new BigDecimal("50000.00"));
        cardDto.setAvailableLimit(new BigDecimal("70000.00"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cardService.updateCard("CARD1001", cardDto);
        });

        assertEquals("Available limit cannot exceed credit limit", ex.getMessage());
    }
}
