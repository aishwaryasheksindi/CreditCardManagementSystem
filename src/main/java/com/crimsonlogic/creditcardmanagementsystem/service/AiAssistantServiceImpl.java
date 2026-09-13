package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatHistoryResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChatResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class AiAssistantServiceImpl implements IAiAssistantService {

    private static final BigDecimal MIN_EMI_PURCHASE_AMOUNT = new BigDecimal("3000.00");

    private final CustomerRepository customerRepository;
    private final CurrentUserContext currentUserContext;
    private final IChatHistoryService chatHistoryService;
    private final ICardService cardService;
    private final TransactionRepository transactionRepository;
    private final IStatementService statementService;
    private final IRewardService rewardService;

    public AiAssistantServiceImpl(CustomerRepository customerRepository,
                                  CurrentUserContext currentUserContext,
                                  IChatHistoryService chatHistoryService,
                                  ICardService cardService,
                                  TransactionRepository transactionRepository,
                                  IStatementService statementService,
                                  IRewardService rewardService) {
        this.customerRepository = customerRepository;
        this.currentUserContext = currentUserContext;
        this.chatHistoryService = chatHistoryService;
        this.cardService = cardService;
        this.transactionRepository = transactionRepository;
        this.statementService = statementService;
        this.rewardService = rewardService;
    }

    @Override
    public ChatResponseDto processChat(ChatRequestDto requestDto) {
        String customerId = requestDto.getCustomerId();

        // 1. Validate Customer Exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        // 2. Ownership enforcement (Cardholder can only query their own customerId)
        currentUserContext.assertCustomerOwnership(customerId);

        String message = requestDto.getMessage();
        String lowerMsg = message != null ? message.toLowerCase(Locale.ROOT) : "";

        String answer;
        List<String> dataSourcesUsed = new ArrayList<>();

        // 3. Rule-based Intent Matching
        if (isPaymentDueDateIntent(lowerMsg)) {
            dataSourcesUsed.addAll(List.of("statements", "cards"));
            answer = handlePaymentDueDate(customerId);
        } else if (isUtilizationIntent(lowerMsg)) {
            dataSourcesUsed.add("cards");
            answer = handleCreditUtilization(customerId);
        } else if (isRewardBalanceIntent(lowerMsg)) {
            dataSourcesUsed.add("rewards");
            answer = handleRewardBalance(customerId);
        } else if (isEmiEligibilityIntent(lowerMsg)) {
            dataSourcesUsed.addAll(List.of("transactions", "cards"));
            answer = handleEmiEligiblePurchases(customerId);
        } else if (isRecentTransactionsIntent(lowerMsg)) {
            dataSourcesUsed.add("transactions");
            answer = handleRecentTransactions(customerId);
        } else if (isSpendingByCategoryIntent(lowerMsg)) {
            dataSourcesUsed.add("transactions");
            answer = handleSpendingByCategory(customerId, lowerMsg);
        } else {
            answer = buildCapabilitiesFallback();
        }

        // 4. Persist Exchange via Existing ChatHistoryService
        LocalDateTime now = LocalDateTime.now();
        ChatHistoryRequestDto historyRequest = new ChatHistoryRequestDto(
                customerId,
                requestDto.getMessage(),
                answer,
                now
        );
        ChatHistoryResponseDto savedHistory = chatHistoryService.createChat(historyRequest);

        String chatId = savedHistory != null && savedHistory.getChatId() != null
                ? savedHistory.getChatId()
                : "CHAT_" + System.currentTimeMillis();

        return new ChatResponseDto(chatId, answer, now, dataSourcesUsed);
    }

    // --- Intent Matchers ---

    private boolean isPaymentDueDateIntent(String msg) {
        return msg.contains("due date")
                || msg.contains("payment due")
                || msg.contains("when is my payment")
                || msg.contains("bill due")
                || msg.contains("next payment")
                || msg.contains("minimum due");
    }

    private boolean isUtilizationIntent(String msg) {
        return msg.contains("utilization")
                || msg.contains("available credit")
                || msg.contains("available limit")
                || msg.contains("credit limit")
                || msg.contains("credit balance");
    }

    private boolean isRewardBalanceIntent(String msg) {
        return msg.contains("reward")
                || msg.contains("reward points")
                || msg.contains("how many points")
                || msg.contains("points balance");
    }

    private boolean isEmiEligibilityIntent(String msg) {
        return msg.contains("emi")
                || msg.contains("convert to emi")
                || msg.contains("installment")
                || msg.contains("installments");
    }

    private boolean isRecentTransactionsIntent(String msg) {
        return msg.contains("recent transaction")
                || msg.contains("recent transactions")
                || msg.contains("latest transaction")
                || msg.contains("latest transactions")
                || msg.contains("show my transactions")
                || msg.contains("last transaction");
    }

    private boolean isSpendingByCategoryIntent(String msg) {
        return msg.contains("spend")
                || msg.contains("spent")
                || msg.contains("spending")
                || msg.contains("how much did i spend")
                || msg.contains("category");
    }

    // --- Intent Handlers with Strict Separation of Facts and Guidance ---

    private String handleSpendingByCategory(String customerId, String msg) {
        List<Transaction> txns = transactionRepository
                .findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId);

        String detectedCategory = detectCategoryFromPrompt(msg);

        if (detectedCategory != null) {
            List<Transaction> filtered = txns.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getCategoryName() != null)
                    .filter(t -> t.getCategory().getCategoryName().equalsIgnoreCase(detectedCategory))
                    .toList();

            BigDecimal totalSpent = filtered.stream()
                    .map(t -> t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String facts;
            if (filtered.isEmpty()) {
                facts = String.format("[Account Facts]\nYou have no recorded transactions in the '%s' category.", detectedCategory);
            } else {
                facts = String.format("[Account Facts]\nYour total spending in '%s' is ₹%,.2f across %d transaction(s).",
                        detectedCategory, totalSpent, filtered.size());
            }

            String guidance = "\n\n[Guidance]\nMonitoring category-level expenses helps maintain budget goals and ensures you leverage appropriate reward categories.";
            return facts + guidance;
        } else {
            BigDecimal totalSpent = txns.stream()
                    .map(t -> t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String facts = String.format("[Account Facts]\nTotal spending across all categories is ₹%,.2f across %d transaction(s).",
                    totalSpent, txns.size());
            String guidance = "\n\n[Guidance]\nTo view detailed category spending, you can ask for a specific category like 'How much did I spend on Dining?'.";
            return facts + guidance;
        }
    }

    private String detectCategoryFromPrompt(String msg) {
        if (msg.contains("dining") || msg.contains("food") || msg.contains("restaurant")) {
            return "Dining";
        }
        if (msg.contains("travel") || msg.contains("flight") || msg.contains("hotel")) {
            return "Travel";
        }
        if (msg.contains("shopping") || msg.contains("retail") || msg.contains("clothing")) {
            return "Shopping";
        }
        if (msg.contains("groceries") || msg.contains("grocery") || msg.contains("supermarket")) {
            return "Groceries";
        }
        if (msg.contains("electronics") || msg.contains("gadget")) {
            return "Electronics";
        }
        if (msg.contains("movies") || msg.contains("entertainment")) {
            return "Entertainment";
        }
        if (msg.contains("fuel") || msg.contains("petrol") || msg.contains("gas")) {
            return "Fuel";
        }
        return null;
    }

    private String handleCreditUtilization(String customerId) {
        List<CardResponseDto> cards = cardService.getCardsByCustomerId(customerId);

        if (cards == null || cards.isEmpty()) {
            return "[Account Facts]\nNo credit cards found for your account.\n\n[Guidance]\nPlease apply for or link a CardNest credit card to view credit utilization.";
        }

        BigDecimal totalLimit = BigDecimal.ZERO;
        BigDecimal totalAvailable = BigDecimal.ZERO;

        for (CardResponseDto card : cards) {
            if (card.getCreditLimit() != null) {
                totalLimit = totalLimit.add(card.getCreditLimit());
            }
            if (card.getAvailableLimit() != null) {
                totalAvailable = totalAvailable.add(card.getAvailableLimit());
            }
        }

        BigDecimal usedBalance = totalLimit.subtract(totalAvailable);
        double utilizationPercent = 0.0;
        if (totalLimit.compareTo(BigDecimal.ZERO) > 0) {
            utilizationPercent = usedBalance.divide(totalLimit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        String facts = String.format("[Account Facts]\nTotal Credit Limit: ₹%,.2f\nAvailable Credit: ₹%,.2f\nCurrent Used Balance: ₹%,.2f\nCredit Utilization: %.1f%% across %d card(s).",
                totalLimit, totalAvailable, usedBalance, utilizationPercent, cards.size());

        String guidance = "\n\n[Guidance]\nMaintaining credit utilization under 30% helps protect your credit score and avoid unnecessary financial stress.";
        return facts + guidance;
    }

    private String handleRecentTransactions(String customerId) {
        List<Transaction> txns = transactionRepository
                .findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId);

        if (txns == null || txns.isEmpty()) {
            return "[Account Facts]\nNo recent transactions found on your account.\n\n[Guidance]\nTransactions will appear here in real-time as you use your cards.";
        }

        List<Transaction> recent = txns.stream().limit(5).toList();
        StringBuilder sb = new StringBuilder("[Account Facts]\nYour recent transactions:\n");
        int index = 1;
        for (Transaction t : recent) {
            String merchantName = t.getMerchant() != null && t.getMerchant().getMerchantName() != null
                    ? t.getMerchant().getMerchantName()
                    : "Merchant";
            String dateStr = t.getTransactionDate() != null
                    ? t.getTransactionDate().toLocalDate().toString()
                    : "Recent";
            sb.append(String.format("%d. ₹%,.2f at %s on %s (ID: %s)\n",
                    index++, t.getAmount(), merchantName, dateStr, t.getTransactionId()));
        }

        String guidance = "[Guidance]\nReview recent transactions periodically. If you see any unrecognized charges, report them immediately.";
        return sb.toString() + "\n" + guidance;
    }

    private String handlePaymentDueDate(String customerId) {
        List<CardResponseDto> cards = cardService.getCardsByCustomerId(customerId);

        if (cards == null || cards.isEmpty()) {
            return "[Account Facts]\nNo credit cards associated with your profile.\n\n[Guidance]\nPlease add an active credit card to track your billing and payment schedules.";
        }

        StatementResponseDto nextStatement = null;
        String relevantCardRef = null;

        for (CardResponseDto card : cards) {
            List<StatementResponseDto> stmts = statementService.getStatementsByCardId(card.getCardId());
            if (stmts != null && !stmts.isEmpty()) {
                StatementResponseDto latest = stmts.stream()
                        .max(Comparator.comparing(StatementResponseDto::getStatementDate, Comparator.nullsLast(Comparator.naturalOrder())))
                        .orElse(null);
                if (latest != null && latest.getDueDate() != null) {
                    if (nextStatement == null || (nextStatement.getDueDate() != null && latest.getDueDate().isBefore(nextStatement.getDueDate()))) {
                        nextStatement = latest;
                        relevantCardRef = card.getCardReference() != null ? card.getCardReference() : card.getCardId();
                    }
                }
            }
        }

        if (nextStatement == null || nextStatement.getDueDate() == null) {
            return "[Account Facts]\nNo current statements with upcoming payment due dates found.\n\n[Guidance]\nStatements are generated monthly at the close of your billing cycle.";
        }

        String last4 = relevantCardRef.length() >= 4 ? relevantCardRef.substring(relevantCardRef.length() - 4) : relevantCardRef;
        String facts = String.format("[Account Facts]\nNext Payment Due: %s (Card ending in %s)\n- Minimum Due: ₹%,.2f\n- Closing Balance: ₹%,.2f",
                nextStatement.getDueDate(),
                last4,
                nextStatement.getMinimumDue() != null ? nextStatement.getMinimumDue() : BigDecimal.ZERO,
                nextStatement.getClosingBalance() != null ? nextStatement.getClosingBalance() : BigDecimal.ZERO);

        String guidance = "\n\n[Guidance]\nPaying the full closing balance on or before the due date avoids interest charges. Always pay at least the minimum due to avoid late payment penalties.";
        return facts + guidance;
    }

    private String handleEmiEligiblePurchases(String customerId) {
        List<Transaction> txns = transactionRepository
                .findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId);

        List<Transaction> eligible = txns.stream()
                .filter(t -> t.getCard() != null && t.getCard().getCardStatus() == CardStatus.ACTIVE)
                .filter(t -> t.getTransactionType() != TransactionType.EMI)
                .filter(t -> t.getAmount() != null && t.getAmount().compareTo(MIN_EMI_PURCHASE_AMOUNT) >= 0)
                .limit(5)
                .toList();

        if (eligible.isEmpty()) {
            return "[Account Facts]\nNo transactions currently meet the eligibility threshold of ₹3,000.00 for EMI conversion.\n\n[Guidance]\nPurchases of ₹3,000 or higher on active credit cards can be converted into flexible monthly EMIs.";
        }

        StringBuilder sb = new StringBuilder("[Account Facts]\nEligible purchases for EMI conversion (amount ≥ ₹3,000.00):\n");
        int index = 1;
        for (Transaction t : eligible) {
            String merchantName = t.getMerchant() != null && t.getMerchant().getMerchantName() != null
                    ? t.getMerchant().getMerchantName()
                    : "Merchant";
            sb.append(String.format("%d. ₹%,.2f at %s (Transaction ID: %s)\n",
                    index++, t.getAmount(), merchantName, t.getTransactionId()));
        }

        String guidance = "[Guidance]\nConverting large one-time purchases to EMIs (3 to 12 months) can help manage monthly cash flow while keeping installment amounts predictable.";
        return sb.toString() + "\n" + guidance;
    }

    private String handleRewardBalance(String customerId) {
        try {
            RewardResponseDto reward = rewardService.getRewardByCustomerId(customerId);
            if (reward == null) {
                return "[Account Facts]\nNo reward account found for your profile.\n\n[Guidance]\nReward points are automatically credited to your profile as you spend on eligible categories.";
            }

            String facts = String.format("[Account Facts]\nReward Points Balance: %,d points\n- Total Earned: %,d points\n- Total Redeemed: %,d points\n- Expired Points: %,d points",
                    reward.getBalancePoints() != null ? reward.getBalancePoints() : 0,
                    reward.getEarnedPoints() != null ? reward.getEarnedPoints() : 0,
                    reward.getRedeemedPoints() != null ? reward.getRedeemedPoints() : 0,
                    reward.getExpiredPoints() != null ? reward.getExpiredPoints() : 0);

            String guidance = "\n\n[Guidance]\nYou can redeem accumulated reward points for cashback, shopping vouchers, or flight/hotel discounts via the Rewards section.";
            return facts + guidance;
        } catch (ResourceNotFoundException e) {
            return "[Account Facts]\nNo active reward points account located for your profile.\n\n[Guidance]\nPoints accrue with card purchases and can be viewed once your initial transactions settle.";
        }
    }

    private String buildCapabilitiesFallback() {
        return """
                [Capabilities]
                I am your CardNest AI Financial Assistant. I can assist you with your account details in the following areas:
                - Spending by Category: "How much did I spend on Dining?"
                - Credit Utilization: "What is my credit utilization / available credit?"
                - Recent Transactions: "Show my recent transactions"
                - Payment Due Date: "When is my payment due?"
                - EMI Eligibility: "Which purchases can I convert to EMI?"
                - Reward Balance: "How many reward points do I have?"

                [Guidance]
                Please ask a question related to one of the supported areas above to retrieve factual information about your account.
                """.stripTrailing();
    }
}
