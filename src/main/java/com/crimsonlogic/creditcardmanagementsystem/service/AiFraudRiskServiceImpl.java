package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiFraudRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiFraudRiskServiceImpl implements IAiFraudRiskService {

    // Deterministic Scoring Weights (Total = 100 points maximum):
    // 1. Amount Deviation: Max 45 points (High deviation from customer's historical baseline)
    // 2. Location Novelty: Max 30 points (Transaction initiated in an unseen geographical location)
    // 3. Velocity: Max 15 points (> 3 transactions within a 1-hour window on this card)
    // 4. Category Novelty: Max 10 points (Merchant category previously unseen for this customer)
    private static final int AMOUNT_EXTREME_POINTS = 45; // > 4.0x average
    private static final int AMOUNT_HIGH_POINTS = 30;    // > 2.5x to 4.0x average
    private static final int AMOUNT_MODERATE_POINTS = 15;// > 1.5x to 2.5x average
    private static final int LOCATION_NOVELTY_POINTS = 30;
    private static final int VELOCITY_HIGH_POINTS = 15;
    private static final int CATEGORY_NOVELTY_POINTS = 10;

    private static final String MODEL_VERSION = "v1.0-ai";

    private final TransactionRepository transactionRepository;
    private final IRiskScoreService riskScoreService;
    private final IFraudAlertService fraudAlertService;
    private final GeminiApiClient geminiApiClient;
    private final CurrentUserContext currentUserContext;

    public AiFraudRiskServiceImpl(TransactionRepository transactionRepository,
                                  IRiskScoreService riskScoreService,
                                  IFraudAlertService fraudAlertService,
                                  GeminiApiClient geminiApiClient,
                                  CurrentUserContext currentUserContext) {
        this.transactionRepository = transactionRepository;
        this.riskScoreService = riskScoreService;
        this.fraudAlertService = fraudAlertService;
        this.geminiApiClient = geminiApiClient;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public AiFraudRiskResponseDto getFraudRisk(String transactionId) {
        // 1. Load transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        // 2. Load card and customer
        Card card = transaction.getCard();
        if (card == null) {
            throw new IllegalArgumentException("Transaction does not have an associated card");
        }

        Customer customer = card.getCustomer();
        if (customer == null) {
            throw new IllegalArgumentException("Card does not have an associated customer");
        }

        // 3. Ownership enforcement: Ensure logged-in customer owns this transaction
        currentUserContext.assertCustomerOwnership(customer.getCustomerId());

        // 4. Load the customer's last 20 transactions excluding the current one
        List<Transaction> customerTransactions = transactionRepository
                .findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customer.getCustomerId());

        List<Transaction> history = customerTransactions.stream()
                .filter(t -> !t.getTransactionId().equals(transactionId))
                .limit(20)
                .toList();

        // 5. Compute deterministic signals and assemble risk factors
        List<String> factorList = new ArrayList<>();
        int amountScore = calculateAmountDeviationScore(transaction, history, factorList);
        int locationScore = calculateLocationNoveltyScore(transaction, history, factorList);
        int velocityScore = calculateVelocityScore(transaction, card, factorList);
        int categoryScore = calculateCategoryNoveltyScore(transaction, history, factorList);

        int totalScore = Math.min(100, Math.max(0, amountScore + locationScore + velocityScore + categoryScore));
        String riskLevel = deriveRiskLevel(totalScore);

        String riskFactors = factorList.isEmpty()
                ? "Normal transaction parameters; no significant risk factors detected"
                : String.join("; ", factorList);

        // 6. Gemini Explanation Layer with Graceful Deterministic Fallback
        String explanation = geminiApiClient.getFraudExplanation(totalScore, riskFactors)
                .orElse(riskFactors);

        // 7. Persist RiskScore via existing IRiskScoreService
        RiskScoreRequestDto riskScoreRequest = new RiskScoreRequestDto(
                transactionId,
                totalScore,
                MODEL_VERSION,
                riskFactors
        );
        RiskScoreResponseDto savedRiskScore = riskScoreService.createRiskScore(riskScoreRequest);

        // 8. If riskLevel is HIGH: also create a FraudAlert via existing IFraudAlertService
        boolean fraudAlertCreated = false;
        if ("HIGH".equalsIgnoreCase(riskLevel)) {
            FraudAlertRequestDto fraudAlertRequest = new FraudAlertRequestDto(
                    transactionId,
                    savedRiskScore.getRiskScoreId(),
                    "OPEN",
                    explanation,
                    null
            );
            fraudAlertService.createFraudAlert(fraudAlertRequest);
            fraudAlertCreated = true;
        }

        // 9. Return AiFraudRiskResponseDto
        return new AiFraudRiskResponseDto(
                transactionId,
                totalScore,
                riskLevel,
                riskFactors,
                explanation,
                savedRiskScore.getRiskScoreId(),
                fraudAlertCreated
        );
    }

    /**
     * Amount Deviation: Compares current transaction amount to customer's historical average.
     */
    private int calculateAmountDeviationScore(Transaction transaction, List<Transaction> history, List<String> factorList) {
        if (history.isEmpty() || transaction.getAmount() == null) {
            return 0;
        }

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        for (Transaction t : history) {
            if (t.getAmount() != null) {
                sum = sum.add(t.getAmount());
                count++;
            }
        }

        if (count == 0 || sum.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal average = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        if (average.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        double ratio = transaction.getAmount().divide(average, 2, RoundingMode.HALF_UP).doubleValue();

        if (ratio > 4.0) {
            factorList.add(String.format("Amount %.1fx customer average", ratio));
            return AMOUNT_EXTREME_POINTS;
        } else if (ratio > 2.5) {
            factorList.add(String.format("Amount %.1fx customer average", ratio));
            return AMOUNT_HIGH_POINTS;
        } else if (ratio > 1.5) {
            factorList.add(String.format("Amount %.1fx customer average", ratio));
            return AMOUNT_MODERATE_POINTS;
        }
        return 0;
    }

    /**
     * Location Novelty: Compares transactionLocation against locations seen in customer's recent transactions.
     */
    private int calculateLocationNoveltyScore(Transaction transaction, List<Transaction> history, List<String> factorList) {
        String currentLocation = transaction.getTransactionLocation();
        if (currentLocation == null || currentLocation.isBlank() || history.isEmpty()) {
            return 0;
        }

        boolean seen = history.stream()
                .map(Transaction::getTransactionLocation)
                .filter(loc -> loc != null && !loc.isBlank())
                .anyMatch(loc -> loc.trim().equalsIgnoreCase(currentLocation.trim()));

        if (!seen) {
            factorList.add(String.format("New location: %s", currentLocation.trim()));
            return LOCATION_NOVELTY_POINTS;
        }
        return 0;
    }

    /**
     * Velocity: Counts transactions within 1-hour window on this card.
     */
    private int calculateVelocityScore(Transaction transaction, Card card, List<String> factorList) {
        LocalDateTime txnTime = transaction.getTransactionDate() != null
                ? transaction.getTransactionDate()
                : LocalDateTime.now();

        LocalDateTime windowStart = txnTime.minusHours(1);
        List<Transaction> windowTxns = transactionRepository
                .findByCard_CardIdAndTransactionDateBetween(card.getCardId(), windowStart, txnTime);

        if (windowTxns != null && windowTxns.size() > 3) {
            factorList.add(String.format("High transaction velocity: %d transactions in 1-hour window", windowTxns.size()));
            return VELOCITY_HIGH_POINTS;
        }
        return 0;
    }

    /**
     * Category Novelty: Checks if current category was seen in customer's recent transactions.
     */
    private int calculateCategoryNoveltyScore(Transaction transaction, List<Transaction> history, List<String> factorList) {
        if (transaction.getCategory() == null || history.isEmpty()) {
            return 0;
        }

        String currentCatId = transaction.getCategory().getCategoryId();
        String currentCatName = transaction.getCategory().getCategoryName();

        boolean seen = history.stream()
                .map(Transaction::getCategory)
                .filter(cat -> cat != null)
                .anyMatch(cat -> (currentCatId != null && currentCatId.equals(cat.getCategoryId()))
                        || (currentCatName != null && currentCatName.equalsIgnoreCase(cat.getCategoryName())));

        if (!seen) {
            String categoryLabel = currentCatName != null && !currentCatName.isBlank()
                    ? currentCatName
                    : (currentCatId != null ? currentCatId : "Unknown");
            factorList.add(String.format("New merchant category: %s", categoryLabel));
            return CATEGORY_NOVELTY_POINTS;
        }
        return 0;
    }

    /**
     * Maps final score to riskLevel according to exact PDF thresholds:
     * 0-30: LOW
     * 31-70: MEDIUM
     * 71-100: HIGH
     */
    private String deriveRiskLevel(int score) {
        if (score <= 30) {
            return "LOW";
        } else if (score <= 70) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
}
