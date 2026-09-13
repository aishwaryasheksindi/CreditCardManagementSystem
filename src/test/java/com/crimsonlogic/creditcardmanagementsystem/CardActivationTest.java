package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardActivationOtpResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardActivationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardTypeRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.MerchantRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionCategoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.CardServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardService;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardStatusHistoryService;
import com.crimsonlogic.creditcardmanagementsystem.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardActivationTest {

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

    @Mock
    private ICardStatusHistoryService cardStatusHistoryService;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private CardServiceImpl cardService;

    // Separate mocks for transaction business rule testing
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private TransactionCategoryRepository categoryRepository;

    @Mock
    private ICardService mockedCardService;

    private Customer customer;
    private CardType cardType;
    private Card testCard;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setKycStatus(KycStatus.VERIFIED);

        cardType = new CardType();
        cardType.setCardTypeId("CT1001");

        testCard = new Card();
        testCard.setCardId("CARD1001");
        testCard.setCardReference("TOK-CARD1001");
        testCard.setCustomer(customer);
        testCard.setCardType(cardType);
        testCard.setCardStatus(CardStatus.INACTIVE);
        testCard.setCreditLimit(new BigDecimal("50000.00"));
        testCard.setAvailableLimit(new BigDecimal("50000.00"));
    }

    @Test
    void testAddCard_AlwaysCreatesCardWithStatusInactive() {
        CardRequestDto cardDto = new CardRequestDto();
        cardDto.setCustomerId("CUST1001");
        cardDto.setCardTypeId("CT1001");
        cardDto.setCreditLimit(new BigDecimal("50000.00"));
        cardDto.setAvailableLimit(new BigDecimal("50000.00"));
        // Intentionally pass ACTIVE in DTO to prove it is overridden to INACTIVE
        cardDto.setCardStatus(CardStatus.ACTIVE);
        cardDto.setBillingCycle(1);
        cardDto.setInterestRate(new BigDecimal("15.0"));
        cardDto.setAnnualFee(BigDecimal.ZERO);
        cardDto.setExpiryDate(LocalDate.now().plusYears(3));
        cardDto.setIssuanceDate(LocalDate.now());

        when(cardRepository.existsById(any())).thenReturn(false);
        when(customerRepository.findById("CUST1001")).thenReturn(Optional.of(customer));
        when(cardTypeRepository.findById("CT1001")).thenReturn(Optional.of(cardType));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        CardResponseDto response = cardService.addCard(cardDto);

        assertNotNull(response);
        assertEquals(CardStatus.INACTIVE, response.getCardStatus(), "Newly created card must always be INACTIVE");

        // Verify status history recorded with INACTIVE
        ArgumentCaptor<CardStatusHistoryRequestDto> historyCaptor = ArgumentCaptor.forClass(CardStatusHistoryRequestDto.class);
        verify(cardStatusHistoryService, times(1)).addCardStatusHistory(historyCaptor.capture());
        assertEquals(CardStatus.INACTIVE, historyCaptor.getValue().getStatus());
    }

    @Test
    void testRequestActivationOtp_Success() {
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST1001");

        CardActivationOtpResponseDto otpResponse = cardService.requestActivationOtp("CARD1001");

        assertNotNull(otpResponse);
        assertEquals("CARD1001", otpResponse.getCardId());
        assertNotNull(otpResponse.getOtp());
        assertEquals(6, otpResponse.getOtp().length(), "OTP must be 6 digits");
        assertNotNull(otpResponse.getExpiresAt());
        assertTrue(otpResponse.getExpiresAt().isAfter(LocalDateTime.now()));

        verify(currentUserContext, times(1)).assertCustomerOwnership("CUST1001");
        verify(auditLogService, times(1)).logAction(any(), eq(AuditAction.UPDATE), eq("Card"), eq("CARD1001"), contains("Activation OTP requested"));
    }

    @Test
    void testRequestActivationOtp_CardAlreadyActive_ThrowsException() {
        testCard.setCardStatus(CardStatus.ACTIVE);
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST1001");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cardService.requestActivationOtp("CARD1001"));

        assertEquals("Card is already active", ex.getMessage());
    }

    @Test
    void testRequestActivationOtp_CrossCustomer_ThrowsAccessDeniedException() {
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));
        doThrow(new AccessDeniedException("You are not authorized to access this resource"))
                .when(currentUserContext).assertCustomerOwnership("CUST1001");

        assertThrows(AccessDeniedException.class, () ->
                cardService.requestActivationOtp("CARD1001"));
    }

    @Test
    void testActivateCard_Success_TransitionsToActiveAndLogsAudit() {
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST1001");

        // 1. Request OTP
        CardActivationOtpResponseDto otpResponse = cardService.requestActivationOtp("CARD1001");
        String generatedOtp = otpResponse.getOtp();

        // 2. Activate card with valid OTP
        CardActivationRequestDto activateDto = new CardActivationRequestDto(generatedOtp);
        CardResponseDto result = cardService.activateCard("CARD1001", activateDto);

        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE, result.getCardStatus(), "Card should transition to ACTIVE");

        // Verify status history
        ArgumentCaptor<CardStatusHistoryRequestDto> historyCaptor = ArgumentCaptor.forClass(CardStatusHistoryRequestDto.class);
        verify(cardStatusHistoryService, times(1)).addCardStatusHistory(historyCaptor.capture());
        assertEquals(CardStatus.ACTIVE, historyCaptor.getValue().getStatus());

        // Verify audit log
        verify(auditLogService, times(1)).logAction(any(), eq(AuditAction.STATUS_CHANGE), eq("Card"), eq("CARD1001"), contains("Card activated successfully"));
    }

    @Test
    void testActivateCard_WrongOtp_ThrowsException_CardRemainsInactive() {
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST1001");

        // 1. Request OTP
        cardService.requestActivationOtp("CARD1001");

        // 2. Submit wrong OTP
        CardActivationRequestDto wrongDto = new CardActivationRequestDto("000000");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cardService.activateCard("CARD1001", wrongDto));

        assertEquals("Invalid activation OTP", ex.getMessage());
        assertEquals(CardStatus.INACTIVE, testCard.getCardStatus(), "Card must remain INACTIVE");
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void testActivateCard_ExpiredOtp_ThrowsException() {
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST1001");

        // Request OTP
        cardService.requestActivationOtp("CARD1001");

        // Simulate expired OTP by directly putting expired data into store
        cardService.activationOtpStore.put("CARD1001", new CardServiceImpl.OtpData("123456", LocalDateTime.now().minusMinutes(1)));

        CardActivationRequestDto req = new CardActivationRequestDto("123456");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cardService.activateCard("CARD1001", req));

        assertTrue(ex.getMessage().contains("Activation OTP has expired"));
        assertEquals(CardStatus.INACTIVE, testCard.getCardStatus());
    }

    @Test
    void testActivateCard_NoOtpRequested_ThrowsException() {
        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST1001");

        CardActivationRequestDto req = new CardActivationRequestDto("123456");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cardService.activateCard("CARD1001", req));

        assertTrue(ex.getMessage().contains("No activation OTP requested"));
    }

    @Test
    void testTransaction_OnInactiveCard_ThrowsIllegalArgumentException() {
        TransactionServiceImpl transactionService = new TransactionServiceImpl(
                transactionRepository,
                cardRepository,
                merchantRepository,
                categoryRepository,
                mockedCardService,
                currentUserContext
        );

        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(testCard));

        TransactionRequestDto txnDto = new TransactionRequestDto();
        txnDto.setCardId("CARD1001");
        txnDto.setAmount(new BigDecimal("100.00"));
        txnDto.setTransactionType(TransactionType.PURCHASE);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                transactionService.addTransaction(txnDto));

        assertTrue(ex.getMessage().contains("Card is INACTIVE — transactions are not allowed on this card"),
                "Transactions on INACTIVE card must be blocked with a clear message");
    }
}
