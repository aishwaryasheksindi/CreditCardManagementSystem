package com.crimsonlogic.creditcardmanagementsystem.utility;

import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpendingAnalysisUtil {

    private SpendingAnalysisUtil() {
    }

    /**
     * Aggregates total spending amounts grouped by category name.
     * Reusable across Spending Analysis (Batch B) and Reward Recommendation (Batch E).
     */
    public static Map<String, BigDecimal> aggregateCategoryTotals(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            String categoryName = resolveCategoryName(t);
            BigDecimal amount = t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO;
            categoryTotals.merge(categoryName, amount, BigDecimal::add);
        }
        return categoryTotals;
    }

    /**
     * Computes the percentage of total spending contributed by each category.
     */
    public static Map<String, Double> calculateCategoryPercentages(Map<String, BigDecimal> categoryTotals, BigDecimal totalSpending) {
        if (categoryTotals == null || categoryTotals.isEmpty() || totalSpending == null || totalSpending.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyMap();
        }

        Map<String, Double> percentages = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
            double pct = entry.getValue()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalSpending, 2, RoundingMode.HALF_UP)
                    .doubleValue();
            percentages.put(entry.getKey(), pct);
        }
        return percentages;
    }

    /**
     * Finds top N merchants ranked by aggregate spending amount in descending order.
     */
    public static List<String> findTopMerchants(List<Transaction> transactions, int limit) {
        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, BigDecimal> merchantTotals = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            String merchantName = resolveMerchantName(t);
            BigDecimal amount = t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO;
            merchantTotals.merge(merchantName, amount, BigDecimal::add);
        }

        return merchantTotals.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculates average transaction value.
     */
    public static BigDecimal calculateAverageTransactionValue(BigDecimal totalSpending, int count) {
        if (count == 0 || totalSpending == null || totalSpending.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return totalSpending.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Month-over-Month change percentage.
     * Safely handles previousMonthTotal == 0 without dividing by zero.
     */
    public static Double calculateMonthOverMonthChangePercent(BigDecimal currentTotal, BigDecimal previousTotal) {
        BigDecimal curr = currentTotal != null ? currentTotal : BigDecimal.ZERO;
        BigDecimal prev = previousTotal != null ? previousTotal : BigDecimal.ZERO;

        if (prev.compareTo(BigDecimal.ZERO) == 0) {
            return curr.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }

        return curr.subtract(prev)
                .multiply(BigDecimal.valueOf(100))
                .divide(prev, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Determines the top spending category by aggregate total amount.
     */
    public static String findTopSpendingCategory(Map<String, BigDecimal> categoryTotals) {
        if (categoryTotals == null || categoryTotals.isEmpty()) {
            return null;
        }
        return categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static String resolveCategoryName(Transaction t) {
        if (t.getCategory() != null) {
            if (t.getCategory().getCategoryName() != null && !t.getCategory().getCategoryName().isBlank()) {
                return t.getCategory().getCategoryName();
            } else if (t.getCategory().getCategoryId() != null) {
                return t.getCategory().getCategoryId();
            }
        }
        return "Other";
    }

    private static String resolveMerchantName(Transaction t) {
        if (t.getMerchant() != null) {
            if (t.getMerchant().getMerchantName() != null && !t.getMerchant().getMerchantName().isBlank()) {
                return t.getMerchant().getMerchantName();
            } else if (t.getMerchant().getMerchantId() != null) {
                return t.getMerchant().getMerchantId();
            }
        }
        return "Unknown";
    }
}
