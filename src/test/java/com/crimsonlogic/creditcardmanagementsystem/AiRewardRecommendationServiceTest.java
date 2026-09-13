package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiRewardRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.AiRewardRecommendationServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.service.IRewardRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiRewardRecommendationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IRewardRecommendationService rewardRecommendationService;

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private CurrentUserContext currentUserContext;

    @InjectMocks
    private AiRewardRecommendationServiceImpl aiRewardRecommendationService;

    private Customer sampleCustomer;
    private CardType platinumCardType;
    private CardType fuelCardType;
    private Card platinumCard;
    private Card fuelCard;
    private TransactionCategory travelCat;
    private TransactionCategory diningCat;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId("CUST1001");
        sampleCustomer.setName("Pooja Hegde");

        platinumCardType = new CardType();
        platinumCardType.setTypeName("Platinum");
        platinumCardType.setRewardCategories("Dining,Travel,Shopping");

        platinumCard = new Card();
        platinumCard.setCardId("CARD_PLATINUM");
        platinumCard.setCustomer(sampleCustomer);
        platinumCard.setCardType(platinumCardType);

        fuelCardType = new CardType();
        fuelCardType.setTypeName("Fuel Saver");
        fuelCardType.setRewardCategories("Fuel,Automotive");

        fuelCard = new Card();
        fuelCard.setCardId("CARD_FUEL");
        fuelCard.setCustomer(sampleCustomer);
        fuelCard.setCardType(fuelCardType);

        travelCat = new TransactionCategory("CAT1001", "Travel", "Flights and hotels");
        diningCat = new TransactionCategory("CAT1002", "Dining", "Restaurants");
    }

    @Test
    void testGetRewardRecommendation_MatchedCategory_ReturnsRecommendationAndPersists() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // Transactions where Travel is top spending category (₹5000 vs ₹1000)
        Transaction t1 = new Transaction();
        t1.setTransactionId("TXN1");
        t1.setCard(platinumCard);
        t1.setCategory(travelCat);
        t1.setAmount(new BigDecimal("5000.00"));
        t1.setTransactionStatus(TransactionStatus.COMPLETED);
        t1.setTransactionDate(LocalDateTime.now());

        Transaction t2 = new Transaction();
        t2.setTransactionId("TXN2");
        t2.setCard(platinumCard);
        t2.setCategory(diningCat);
        t2.setAmount(new BigDecimal("1000.00"));
        t2.setTransactionStatus(TransactionStatus.COMPLETED);
        t2.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId))
                .thenReturn(List.of(t1, t2));
        when(cardRepository.findByCustomer_CustomerId(customerId))
                .thenReturn(List.of(platinumCard));

        when(geminiApiClient.getRewardRecommendationReason(eq("Travel"), eq("Platinum")))
                .thenReturn(Optional.of("You spend heavily on Travel and your Platinum card provides exclusive 5x Travel rewards."));

        RewardRecommendationResponseDto savedDto = new RewardRecommendationResponseDto(
                "RR100001", customerId, "TravelRewards", "You spend heavily on Travel and your Platinum card provides exclusive 5x Travel rewards."
        );
        when(rewardRecommendationService.createRecommendation(any(RewardRecommendationRequestDto.class)))
                .thenReturn(savedDto);

        AiRewardRecommendationResponseDto response = aiRewardRecommendationService.getRewardRecommendation(customerId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertTrue(response.isHasRecommendation());
        assertEquals("TravelRewards", response.getOfferName());
        assertEquals("CARD_PLATINUM", response.getMatchedCardId());
        assertEquals("You spend heavily on Travel and your Platinum card provides exclusive 5x Travel rewards.", response.getReason());

        // Verify persisted via IRewardRecommendationService
        ArgumentCaptor<RewardRecommendationRequestDto> captor = ArgumentCaptor.forClass(RewardRecommendationRequestDto.class);
        verify(rewardRecommendationService, times(1)).createRecommendation(captor.capture());
        assertEquals("TravelRewards", captor.getValue().getOfferName());
        assertEquals(customerId, captor.getValue().getCustomerId());
    }

    @Test
    void testGetRewardRecommendation_NoMatchingCategory_ReturnsFalseWithoutFabrication() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);

        // Customer spends on Dining, but only has Fuel Saver card
        Transaction t1 = new Transaction();
        t1.setTransactionId("TXN3");
        t1.setCard(fuelCard);
        t1.setCategory(diningCat);
        t1.setAmount(new BigDecimal("3000.00"));
        t1.setTransactionStatus(TransactionStatus.COMPLETED);
        t1.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId))
                .thenReturn(List.of(t1));
        when(cardRepository.findByCustomer_CustomerId(customerId))
                .thenReturn(List.of(fuelCard));

        AiRewardRecommendationResponseDto response = aiRewardRecommendationService.getRewardRecommendation(customerId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertFalse(response.isHasRecommendation());
        assertNull(response.getOfferName());
        assertNull(response.getMatchedCardId());
        assertTrue(response.getReason().contains("No card currently offers rewards matching your top spending category: Dining"));

        // Verify NOTHING is persisted
        verify(rewardRecommendationService, never()).createRecommendation(any());
        verify(geminiApiClient, never()).getRewardRecommendationReason(anyString(), anyString());
    }

    @Test
    void testGetRewardRecommendation_GeminiFallback_UsesDeterministicReason() {
        String customerId = "CUST1001";
        when(customerRepository.existsById(customerId)).thenReturn(true);

        Transaction t1 = new Transaction();
        t1.setTransactionId("TXN4");
        t1.setCard(platinumCard);
        t1.setCategory(travelCat);
        t1.setAmount(new BigDecimal("4000.00"));
        t1.setTransactionStatus(TransactionStatus.COMPLETED);
        t1.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId))
                .thenReturn(List.of(t1));
        when(cardRepository.findByCustomer_CustomerId(customerId))
                .thenReturn(List.of(platinumCard));

        // Gemini unavailable -> fallback
        when(geminiApiClient.getRewardRecommendationReason(eq("Travel"), eq("Platinum")))
                .thenReturn(Optional.empty());

        AiRewardRecommendationResponseDto response = aiRewardRecommendationService.getRewardRecommendation(customerId);

        assertNotNull(response);
        assertTrue(response.isHasRecommendation());
        assertEquals("You spend heavily on Travel and your Platinum card offers Travel rewards.", response.getReason());
        verify(rewardRecommendationService, times(1)).createRecommendation(any());
    }

    @Test
    void testGetRewardRecommendation_CustomerNotFound_ThrowsResourceNotFoundException() {
        when(customerRepository.existsById("CUST_UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> aiRewardRecommendationService.getRewardRecommendation("CUST_UNKNOWN"));
    }

    @Test
    void testGetRewardRecommendation_UnauthorizedAccess_ThrowsAccessDeniedException() {
        when(customerRepository.existsById("CUST1001")).thenReturn(true);
        doThrow(new AccessDeniedException("You are not authorized to access this resource"))
                .when(currentUserContext).assertCustomerOwnership("CUST1001");

        assertThrows(AccessDeniedException.class,
                () -> aiRewardRecommendationService.getRewardRecommendation("CUST1001"));
    }
}
