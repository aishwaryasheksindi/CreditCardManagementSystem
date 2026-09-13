package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiPaymentRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Payment;
import com.crimsonlogic.creditcardmanagementsystem.entity.Statement;
import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.PaymentRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AiPaymentRiskServiceImpl implements IAiPaymentRiskService {

    // Documented Named Threshold Constants:
    // 1. High Utilization: Card utilization > 70%
    // 2. Minimum-Only Cycles: 2 or more billing cycles where only the minimum amount was paid
    // 3. Late Payment Cycles: 1 or more billing cycles where payment was made past the due date
    public static final double HIGH_UTILIZATION_THRESHOLD = 70.0;
    public static final int MINIMUM_ONLY_CYCLE_THRESHOLD = 2;
    public static final int LATE_CYCLE_THRESHOLD = 1;

    // Payment tolerance for minimum-only matching (within ₹10)
    private static final BigDecimal MIN_PAYMENT_TOLERANCE = new BigDecimal("10.00");

    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final StatementRepository statementRepository;
    private final PaymentRepository paymentRepository;
    private final GeminiApiClient geminiApiClient;
    private final CurrentUserContext currentUserContext;

    public AiPaymentRiskServiceImpl(CustomerRepository customerRepository,
                                    CardRepository cardRepository,
                                    StatementRepository statementRepository,
                                    PaymentRepository paymentRepository,
                                    GeminiApiClient geminiApiClient,
                                    CurrentUserContext currentUserContext) {
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
        this.statementRepository = statementRepository;
        this.paymentRepository = paymentRepository;
        this.geminiApiClient = geminiApiClient;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public AiPaymentRiskResponseDto getPaymentRisk(String customerId) {
        // 1. Validate Customer
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        // 2. Ownership enforcement (Staff bypasses, Customer must match ID)
        currentUserContext.assertCustomerOwnership(customerId);

        // 3. Gather customer's cards
        List<Card> cards = cardRepository.findByCustomer_CustomerId(customerId);

        // Signal 1: Utilization calculation across all cards
        double utilizationPercent = calculateAverageUtilization(cards);

        // Signal 2 & 3: Evaluate recent Statements and matching Payments
        int minimumOnlyCycleCount = 0;
        int lateCycleCount = 0;

        for (Card card : cards) {
            List<Statement> cardStatements = statementRepository.findByCardId(card.getCardId())
                    .stream()
                    .sorted(Comparator.comparing(Statement::getStatementDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .limit(3)
                    .toList();

            for (Statement stmt : cardStatements) {
                LocalDate stmtDate = stmt.getStatementDate();
                LocalDate dueDate = stmt.getDueDate();

                LocalDateTime windowStart = stmtDate != null ? stmtDate.atStartOfDay() : LocalDateTime.now().minusMonths(1);
                LocalDateTime windowEnd = dueDate != null ? dueDate.plusDays(45).atTime(LocalTime.MAX) : LocalDateTime.now().plusMonths(1);

                List<Payment> successfulPayments = paymentRepository.findByCardIdAndPaymentDateBetweenAndPaymentStatus(
                        card.getCardId(), windowStart, windowEnd, PaymentStatus.SUCCESS
                );

                BigDecimal totalPaidInCycle = successfulPayments.stream()
                        .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Minimum-only check: paid close to minimumDue, but less than closingBalance
                if (stmt.getMinimumDue() != null && stmt.getClosingBalance() != null
                        && stmt.getMinimumDue().compareTo(stmt.getClosingBalance()) < 0) {
                    BigDecimal diff = totalPaidInCycle.subtract(stmt.getMinimumDue()).abs();
                    if (diff.compareTo(MIN_PAYMENT_TOLERANCE) <= 0) {
                        minimumOnlyCycleCount++;
                    }
                }

                // Lateness check: any SUCCESS payment dated after dueDate
                if (dueDate != null) {
                    boolean hadLatePayment = successfulPayments.stream()
                            .anyMatch(p -> p.getPaymentDate() != null && p.getPaymentDate().toLocalDate().isAfter(dueDate));
                    if (hadLatePayment) {
                        lateCycleCount++;
                    }
                }
            }
        }

        // 4. Classify LOW/MEDIUM/HIGH based on documented thresholds
        boolean highUtilization = utilizationPercent > HIGH_UTILIZATION_THRESHOLD;
        boolean highMinOnly = minimumOnlyCycleCount >= MINIMUM_ONLY_CYCLE_THRESHOLD;
        boolean hasLate = lateCycleCount >= LATE_CYCLE_THRESHOLD;

        int riskSignalsTriggered = (highUtilization ? 1 : 0) + (highMinOnly ? 1 : 0) + (hasLate ? 1 : 0);

        String riskLevel;
        if (riskSignalsTriggered >= 2) {
            riskLevel = "HIGH";
        } else if (riskSignalsTriggered == 1) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        // 5. Gemini Explanation Layer with Deterministic Fallback
        String fallbackExplanation = buildDeterministicExplanation(riskLevel, utilizationPercent, minimumOnlyCycleCount, lateCycleCount, highUtilization, highMinOnly, hasLate);
        String explanation = geminiApiClient.getPaymentRiskExplanation(riskLevel, utilizationPercent, minimumOnlyCycleCount, lateCycleCount)
                .orElse(fallbackExplanation);

        // 6. Prediction-only: No persistence to database

        // 7. Return AiPaymentRiskResponseDto
        return new AiPaymentRiskResponseDto(
                customerId,
                riskLevel,
                utilizationPercent,
                minimumOnlyCycleCount,
                lateCycleCount,
                explanation
        );
    }

    private double calculateAverageUtilization(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return 0.0;
        }

        double totalUtil = 0.0;
        int countedCards = 0;

        for (Card card : cards) {
            if (card.getCreditLimit() != null && card.getCreditLimit().compareTo(BigDecimal.ZERO) > 0) {
                Statement latestStatement = statementRepository.findTopByCardIdOrderByStatementDateDesc(card.getCardId())
                        .orElse(null);

                BigDecimal closingBal = (latestStatement != null && latestStatement.getClosingBalance() != null)
                        ? latestStatement.getClosingBalance()
                        : BigDecimal.ZERO;

                double util = closingBal.divide(card.getCreditLimit(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();

                totalUtil += util;
                countedCards++;
            }
        }

        if (countedCards == 0) {
            return 0.0;
        }

        return BigDecimal.valueOf(totalUtil / countedCards)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String buildDeterministicExplanation(String riskLevel,
                                                 double utilization,
                                                 int minOnlyCount,
                                                 int lateCount,
                                                 boolean highUtilization,
                                                 boolean highMinOnly,
                                                 boolean hasLate) {
        if ("LOW".equalsIgnoreCase(riskLevel)) {
            return String.format(
                    "Customer exhibits low payment risk with healthy credit utilization (%.1f%%) and on-time repayment history.",
                    utilization
            );
        }

        List<String> triggers = new ArrayList<>();
        if (highUtilization) {
            triggers.add(String.format("High credit utilization of %.1f%% exceeds the 70.0%% threshold", utilization));
        }
        if (highMinOnly) {
            triggers.add(String.format("%d minimum-only payment cycle(s) in the last 3 billing cycles", minOnlyCount));
        }
        if (hasLate) {
            triggers.add(String.format("%d late payment cycle(s) past statement due date", lateCount));
        }

        return String.format(
                "Payment risk evaluated as %s due to behavioral indicators: %s.",
                riskLevel,
                String.join("; ", triggers)
        );
    }
}
