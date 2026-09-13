package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiRewardRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.utility.SpendingAnalysisUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AiRewardRecommendationServiceImpl implements IAiRewardRecommendationService {

    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final IRewardRecommendationService rewardRecommendationService;
    private final GeminiApiClient geminiApiClient;
    private final CurrentUserContext currentUserContext;

    public AiRewardRecommendationServiceImpl(CustomerRepository customerRepository,
                                            CardRepository cardRepository,
                                            TransactionRepository transactionRepository,
                                            IRewardRecommendationService rewardRecommendationService,
                                            GeminiApiClient geminiApiClient,
                                            CurrentUserContext currentUserContext) {
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.rewardRecommendationService = rewardRecommendationService;
        this.geminiApiClient = geminiApiClient;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public AiRewardRecommendationResponseDto getRewardRecommendation(String customerId) {
        // 1. Validate Customer
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        // 2. Ownership enforcement (Staff bypasses, Customer must match ID)
        currentUserContext.assertCustomerOwnership(customerId);

        // 3. Reuse category-breakdown logic from Batch B
        List<Transaction> allTransactions = transactionRepository
                .findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId);

        List<Transaction> activeTransactions = allTransactions.stream()
                .filter(t -> t.getTransactionStatus() != TransactionStatus.FAILED)
                .toList();

        // Focus on current month transactions if available, otherwise all historical transactions
        YearMonth currentYm = YearMonth.now();
        LocalDate currentMonthStart = currentYm.atDay(1);
        LocalDate currentMonthEnd = currentYm.atEndOfMonth();

        List<Transaction> currentMonthTxns = activeTransactions.stream()
                .filter(t -> t.getTransactionDate() != null)
                .filter(t -> {
                    LocalDate d = t.getTransactionDate().toLocalDate();
                    return !d.isBefore(currentMonthStart) && !d.isAfter(currentMonthEnd);
                })
                .toList();

        List<Transaction> targetTxns = !currentMonthTxns.isEmpty() ? currentMonthTxns : activeTransactions;

        Map<String, BigDecimal> categoryTotals = SpendingAnalysisUtil.aggregateCategoryTotals(targetTxns);
        String topCategory = SpendingAnalysisUtil.findTopSpendingCategory(categoryTotals);

        // 4. Load customer cards and check reward categories
        List<Card> cards = cardRepository.findByCustomer_CustomerId(customerId);

        Card matchedCard = null;
        if (topCategory != null && cards != null && !cards.isEmpty()) {
            for (Card card : cards) {
                CardType cardType = card.getCardType();
                if (cardType != null && cardType.getRewardCategories() != null) {
                    String[] categories = cardType.getRewardCategories().split(",");
                    boolean matches = Arrays.stream(categories)
                            .map(String::trim)
                            .anyMatch(cat -> cat.equalsIgnoreCase(topCategory));

                    if (matches) {
                        matchedCard = card;
                        break;
                    }
                }
            }
        }

        // 5. If no match exists: return no recommendation (do NOT fabricate)
        if (matchedCard == null || topCategory == null) {
            String message = topCategory != null
                    ? String.format("No card currently offers rewards matching your top spending category: %s.", topCategory)
                    : "No spending transactions available to recommend rewards.";
            return new AiRewardRecommendationResponseDto(
                    customerId,
                    false,
                    null,
                    message,
                    null
            );
        }

        // 6. Match exists: generate reason via Gemini with deterministic fallback
        String cardTypeName = (matchedCard.getCardType() != null && matchedCard.getCardType().getTypeName() != null)
                ? matchedCard.getCardType().getTypeName()
                : "Credit";

        String fallbackReason = String.format(
                "You spend heavily on %s and your %s card offers %s rewards.",
                topCategory,
                cardTypeName,
                topCategory
        );

        String reason = geminiApiClient.getRewardRecommendationReason(topCategory, cardTypeName)
                .orElse(fallbackReason);

        String offerName = topCategory + "Rewards";

        // 7. Persist via existing IRewardRecommendationService
        RewardRecommendationRequestDto requestDto = new RewardRecommendationRequestDto(
                customerId,
                offerName,
                reason
        );
        rewardRecommendationService.createRecommendation(requestDto);

        // 8. Return response
        return new AiRewardRecommendationResponseDto(
                customerId,
                true,
                offerName,
                reason,
                matchedCard.getCardId()
        );
    }
}
