package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiSpendingAnalysisResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.utility.SpendingAnalysisUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
public class AiSpendingAnalysisServiceImpl implements IAiSpendingAnalysisService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ISpendingInsightService spendingInsightService;
    private final GeminiApiClient geminiApiClient;
    private final CurrentUserContext currentUserContext;

    public AiSpendingAnalysisServiceImpl(TransactionRepository transactionRepository,
                                         CustomerRepository customerRepository,
                                         ISpendingInsightService spendingInsightService,
                                         GeminiApiClient geminiApiClient,
                                         CurrentUserContext currentUserContext) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.spendingInsightService = spendingInsightService;
        this.geminiApiClient = geminiApiClient;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public AiSpendingAnalysisResponseDto getSpendingAnalysis(String customerId) {
        // 1. Validate Customer Exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        // 2. Ownership enforcement (Staff bypasses, Customer must match ID)
        currentUserContext.assertCustomerOwnership(customerId);

        // 3. Define calendar bounds for current month and previous month
        YearMonth currentYm = YearMonth.now();
        LocalDate currentMonthStart = currentYm.atDay(1);
        LocalDate currentMonthEnd = currentYm.atEndOfMonth();

        YearMonth previousYm = currentYm.minusMonths(1);
        LocalDate previousMonthStart = previousYm.atDay(1);
        LocalDate previousMonthEnd = previousYm.atEndOfMonth();

        // 4. Fetch all customer transactions
        List<Transaction> allTransactions = transactionRepository
                .findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId);

        // Filter for current month and previous month (excluding FAILED transactions)
        List<Transaction> currentMonthTxns = allTransactions.stream()
                .filter(t -> t.getTransactionStatus() != TransactionStatus.FAILED)
                .filter(t -> t.getTransactionDate() != null)
                .filter(t -> {
                    LocalDate d = t.getTransactionDate().toLocalDate();
                    return !d.isBefore(currentMonthStart) && !d.isAfter(currentMonthEnd);
                })
                .toList();

        List<Transaction> previousMonthTxns = allTransactions.stream()
                .filter(t -> t.getTransactionStatus() != TransactionStatus.FAILED)
                .filter(t -> t.getTransactionDate() != null)
                .filter(t -> {
                    LocalDate d = t.getTransactionDate().toLocalDate();
                    return !d.isBefore(previousMonthStart) && !d.isAfter(previousMonthEnd);
                })
                .toList();

        // 5. Deterministic calculations from real data
        BigDecimal totalSpending = currentMonthTxns.stream()
                .map(t -> t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal previousMonthTotal = previousMonthTxns.stream()
                .map(t -> t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Map<String, BigDecimal> categoryTotals = SpendingAnalysisUtil.aggregateCategoryTotals(currentMonthTxns);
        Map<String, Double> categoryBreakdown = SpendingAnalysisUtil.calculateCategoryPercentages(categoryTotals, totalSpending);
        List<String> topMerchants = SpendingAnalysisUtil.findTopMerchants(currentMonthTxns, 3);
        BigDecimal avgTxnValue = SpendingAnalysisUtil.calculateAverageTransactionValue(totalSpending, currentMonthTxns.size());
        Double momChangePercent = SpendingAnalysisUtil.calculateMonthOverMonthChangePercent(totalSpending, previousMonthTotal);

        // 6. Gemini Summary Generation with Deterministic Template Fallback
        String fallbackSummary = buildDeterministicSummary(totalSpending, categoryBreakdown, topMerchants, avgTxnValue, momChangePercent, currentMonthTxns.size());
        String summary = geminiApiClient.getSpendingSummary(totalSpending, categoryBreakdown, topMerchants, avgTxnValue, momChangePercent)
                .orElse(fallbackSummary);

        // 7. Persist via existing ISpendingInsightService
        SpendingInsightRequestDto insightRequest = new SpendingInsightRequestDto(
                customerId,
                summary,
                totalSpending,
                currentMonthStart,
                currentMonthEnd
        );
        SpendingInsightResponseDto savedInsight = spendingInsightService.createInsight(insightRequest);

        // 8. Return response
        return new AiSpendingAnalysisResponseDto(
                customerId,
                totalSpending,
                categoryBreakdown,
                topMerchants,
                avgTxnValue,
                momChangePercent,
                summary,
                savedInsight.getInsightId(),
                currentMonthStart,
                currentMonthEnd
        );
    }

    private String buildDeterministicSummary(BigDecimal totalSpending,
                                            Map<String, Double> categoryBreakdown,
                                            List<String> topMerchants,
                                            BigDecimal avgTxnValue,
                                            Double momChangePercent,
                                            int txnCount) {
        if (txnCount == 0 || totalSpending.compareTo(BigDecimal.ZERO) == 0) {
            return "No credit card transactions recorded for the current calendar month.";
        }
        return String.format(
                "Total spending for this month was ₹%s across %d transaction(s) with an average transaction value of ₹%s (%+.1f%% compared to last month). Top spending categories: %s.",
                totalSpending,
                txnCount,
                avgTxnValue,
                momChangePercent != null ? momChangePercent : 0.0,
                categoryBreakdown.keySet().isEmpty() ? "None" : categoryBreakdown.keySet().toString()
        );
    }
}
