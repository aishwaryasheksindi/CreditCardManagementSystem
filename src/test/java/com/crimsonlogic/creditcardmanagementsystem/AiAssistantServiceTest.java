package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Merchant;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.AiAssistantServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardService;
import com.crimsonlogic.creditcardmanagementsystem.service.IChatHistoryService;
import com.crimsonlogic.creditcardmanagementsystem.service.IRewardService;
import com.crimsonlogic.creditcardmanagementsystem.service.IStatementService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAssistantServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CurrentUserContext currentUserContext;

    @Mock
    private IChatHistoryService chatHistoryService;

    @Mock
    private ICardService cardService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IStatementService statementService;

    @Mock
    private IRewardService rewardService;

    @InjectMocks
    private AiAssistantServiceImpl aiAssistantService;

    private Customer testCustomer;
    private Card testCard;
    private CardResponseDto testCardDto;
    private TransactionCategory diningCategory;
    private Merchant starbucksMerchant;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId("CUST_001");

        testCard = new Card();
        testCard.setCardId("CARD_001");
        testCard.setCardReference("411111******1111");
        testCard.setCustomer(testCustomer);
        testCard.setCardStatus(CardStatus.ACTIVE);

        testCardDto = new CardResponseDto();
        testCardDto.setCardId("CARD_001");
        testCardDto.setCardReference("411111******1111");
        testCardDto.setCreditLimit(new BigDecimal("100000.00"));
        testCardDto.setAvailableLimit(new BigDecimal("70000.00"));
        testCardDto.setCardStatus(CardStatus.ACTIVE);

        diningCategory = new TransactionCategory("CAT_DINING", "Dining", "Food & Restaurants");
        starbucksMerchant = new Merchant("MERCH_01", "Starbucks", "Dining", "Bangalore", "info@starbucks.com");

        lenient().when(chatHistoryService.createChat(any(ChatHistoryRequestDto.class)))
                .thenAnswer(inv -> {
                    ChatHistoryRequestDto req = inv.getArgument(0);
                    return new ChatHistoryResponseDto("CHAT_12345", req.getCustomerId(), req.getQuestion(), req.getAnswer(), LocalDateTime.now());
                });
    }

    @Test
    void testProcessChat_CustomerNotFound_ThrowsException() {
        when(customerRepository.existsById("CUST_NONEXISTENT")).thenReturn(false);

        ChatRequestDto req = new ChatRequestDto("CUST_NONEXISTENT", "Hello");
        assertThrows(ResourceNotFoundException.class, () -> aiAssistantService.processChat(req));

        verify(customerRepository, times(1)).existsById("CUST_NONEXISTENT");
    }

    @Test
    void testProcessChat_ForbiddenAccess_ThrowsException() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doThrow(new AccessDeniedException("Access denied")).when(currentUserContext).assertCustomerOwnership("CUST_001");

        ChatRequestDto req = new ChatRequestDto("CUST_001", "Show my transactions");
        assertThrows(AccessDeniedException.class, () -> aiAssistantService.processChat(req));

        verify(currentUserContext, times(1)).assertCustomerOwnership("CUST_001");
    }

    @Test
    void testProcessChat_SpendingByCategoryIntent() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        Transaction t1 = new Transaction("TXN_01", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("1500.00"), "INR", LocalDateTime.now().minusDays(3), "Bangalore", TransactionStatus.COMPLETED);
        Transaction t2 = new Transaction("TXN_02", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("1000.00"), "INR", LocalDateTime.now().minusDays(1), "Bangalore", TransactionStatus.COMPLETED);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(List.of(t1, t2));

        ChatRequestDto req = new ChatRequestDto("CUST_001", "How much did I spend on dining?");
        ChatResponseDto res = aiAssistantService.processChat(req);

        assertNotNull(res);
        assertEquals("CHAT_12345", res.getChatId());
        assertTrue(res.getDataSourcesUsed().contains("transactions"));
        assertTrue(res.getMessage().contains("[Account Facts]"));
        assertTrue(res.getMessage().contains("₹2,500.00"));
        assertTrue(res.getMessage().contains("[Guidance]"));
        verify(chatHistoryService, times(1)).createChat(any(ChatHistoryRequestDto.class));
    }

    @Test
    void testProcessChat_CreditUtilizationIntent() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        when(cardService.getCardsByCustomerId("CUST_001")).thenReturn(List.of(testCardDto));

        ChatRequestDto req = new ChatRequestDto("CUST_001", "What is my credit utilization and available credit?");
        ChatResponseDto res = aiAssistantService.processChat(req);

        assertNotNull(res);
        assertTrue(res.getDataSourcesUsed().contains("cards"));
        assertTrue(res.getMessage().contains("[Account Facts]"));
        assertTrue(res.getMessage().contains("30.0%"));
        assertTrue(res.getMessage().contains("₹70,000.00"));
        assertTrue(res.getMessage().contains("[Guidance]"));
    }

    @Test
    void testProcessChat_RecentTransactionsIntent() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        Transaction t1 = new Transaction("TXN_01", testCard, starbucksMerchant, diningCategory,
                new BigDecimal("450.00"), "INR", LocalDateTime.now().minusDays(1), "Bangalore", TransactionStatus.COMPLETED);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(List.of(t1));

        ChatRequestDto req = new ChatRequestDto("CUST_001", "Show my recent transactions");
        ChatResponseDto res = aiAssistantService.processChat(req);

        assertNotNull(res);
        assertTrue(res.getDataSourcesUsed().contains("transactions"));
        assertTrue(res.getMessage().contains("[Account Facts]"));
        assertTrue(res.getMessage().contains("Starbucks"));
        assertTrue(res.getMessage().contains("₹450.00"));
        assertTrue(res.getMessage().contains("[Guidance]"));
    }

    @Test
    void testProcessChat_PaymentDueDateIntent() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        when(cardService.getCardsByCustomerId("CUST_001")).thenReturn(List.of(testCardDto));

        StatementResponseDto stmt = new StatementResponseDto();
        stmt.setStatementId("STMT_001");
        stmt.setCardId("CARD_001");
        stmt.setStatementDate(LocalDate.of(2026, 9, 1));
        stmt.setDueDate(LocalDate.of(2026, 9, 25));
        stmt.setClosingBalance(new BigDecimal("15000.00"));
        stmt.setMinimumDue(new BigDecimal("1500.00"));

        when(statementService.getStatementsByCardId("CARD_001")).thenReturn(List.of(stmt));

        ChatRequestDto req = new ChatRequestDto("CUST_001", "When is my payment due date?");
        ChatResponseDto res = aiAssistantService.processChat(req);

        assertNotNull(res);
        assertTrue(res.getDataSourcesUsed().contains("statements"));
        assertTrue(res.getDataSourcesUsed().contains("cards"));
        assertTrue(res.getMessage().contains("[Account Facts]"));
        assertTrue(res.getMessage().contains("2026-09-25"));
        assertTrue(res.getMessage().contains("₹1,500.00"));
        assertTrue(res.getMessage().contains("₹15,000.00"));
        assertTrue(res.getMessage().contains("[Guidance]"));
    }

    @Test
    void testProcessChat_EmiEligiblePurchasesIntent() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        Merchant croma = new Merchant("MERCH_02", "Croma Electronics", "Electronics", "Bangalore", "info@croma.com");
        Transaction highValue = new Transaction("TXN_EMI", testCard, croma, diningCategory,
                new BigDecimal("8500.00"), "INR", LocalDateTime.now().minusDays(2), "Bangalore", TransactionStatus.COMPLETED);
        highValue.setTransactionType(TransactionType.PURCHASE);

        when(transactionRepository.findByCard_Customer_CustomerIdOrderByTransactionDateDesc("CUST_001"))
                .thenReturn(List.of(highValue));

        ChatRequestDto req = new ChatRequestDto("CUST_001", "Which purchases can I convert to EMI?");
        ChatResponseDto res = aiAssistantService.processChat(req);

        assertNotNull(res);
        assertTrue(res.getDataSourcesUsed().contains("transactions"));
        assertTrue(res.getMessage().contains("[Account Facts]"));
        assertTrue(res.getMessage().contains("₹8,500.00"));
        assertTrue(res.getMessage().contains("Croma Electronics"));
        assertTrue(res.getMessage().contains("[Guidance]"));
    }

    @Test
    void testProcessChat_RewardBalanceIntent() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        RewardResponseDto reward = new RewardResponseDto("REW_01", "CUST_001", 1200, 300, 0, 100, 1000);
        when(rewardService.getRewardByCustomerId("CUST_001")).thenReturn(reward);

        ChatRequestDto req = new ChatRequestDto("CUST_001", "How many reward points do I have?");
        ChatResponseDto res = aiAssistantService.processChat(req);

        assertNotNull(res);
        assertTrue(res.getDataSourcesUsed().contains("rewards"));
        assertTrue(res.getMessage().contains("[Account Facts]"));
        assertTrue(res.getMessage().contains("1,000 points"));
        assertTrue(res.getMessage().contains("[Guidance]"));
    }

    @Test
    void testProcessChat_UnknownIntent_ReturnsCapabilitiesGuidance() {
        when(customerRepository.existsById("CUST_001")).thenReturn(true);
        doNothing().when(currentUserContext).assertCustomerOwnership("CUST_001");

        ChatRequestDto req = new ChatRequestDto("CUST_001", "Tell me a bedtime story");
        ChatResponseDto res = aiAssistantService.processChat(req);

        assertNotNull(res);
        assertTrue(res.getDataSourcesUsed().isEmpty());
        assertTrue(res.getMessage().contains("[Capabilities]"));
        assertTrue(res.getMessage().contains("[Guidance]"));
    }
}
