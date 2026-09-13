package com.crimsonlogic.creditcardmanagementsystem.utility;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiTenureOptionDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EmiCalculatorUtil {

    public static final BigDecimal MIN_EMI_AMOUNT = new BigDecimal("3000.00");
    public static final List<Integer> STANDARD_TENURES = List.of(3, 6, 9, 12, 18, 24);
    public static final BigDecimal DEFAULT_ANNUAL_INTEREST_RATE = new BigDecimal("15.00");
    public static final BigDecimal PROCESSING_FEE_RATE = new BigDecimal("0.015"); // 1.5%
    public static final BigDecimal MIN_PROCESSING_FEE = new BigDecimal("100.00");

    private EmiCalculatorUtil() {
    }

    /**
     * Calculates the processing fee: 1.5% of principal, minimum ₹100.
     */
    public static BigDecimal calculateProcessingFee(BigDecimal principal) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal fee = principal.multiply(PROCESSING_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        return fee.compareTo(MIN_PROCESSING_FEE) < 0 ? MIN_PROCESSING_FEE : fee;
    }

    /**
     * Calculates reducing-balance monthly EMI amount.
     * Formula: EMI = [P * r * (1 + r)^n] / [(1 + r)^n - 1]
     * where r = annualRate / 12 / 100
     */
    public static BigDecimal calculateMonthlyEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0 || tenureMonths <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / 12.0 / 100.0;
        double pow = Math.pow(1.0 + r, tenureMonths);
        double emi = (p * r * pow) / (pow - 1.0);

        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generates EMI options for standard tenures (3, 6, 9, 12, 18, 24 months).
     */
    public static List<EmiTenureOptionDto> calculateAllTenureOptions(BigDecimal principal, BigDecimal annualRate) {
        List<EmiTenureOptionDto> options = new ArrayList<>();
        BigDecimal fee = calculateProcessingFee(principal);

        for (int tenure : STANDARD_TENURES) {
            BigDecimal monthlyEmi = calculateMonthlyEmi(principal, annualRate, tenure);
            BigDecimal totalRepaid = monthlyEmi.multiply(BigDecimal.valueOf(tenure)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalInterest = totalRepaid.subtract(principal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalPayable = totalRepaid.add(fee).setScale(2, RoundingMode.HALF_UP);

            options.add(new EmiTenureOptionDto(tenure, monthlyEmi, totalInterest, totalPayable, fee));
        }
        return options;
    }

    /**
     * Deterministic recommendation when AI is unavailable or as a baseline fallback.
     * Selects 6 months if affordable (or 3 months for smaller purchases < ₹5000, 12 months for larger purchases > ₹50000).
     */
    public static EmiTenureOptionDto selectDefaultRecommendation(List<EmiTenureOptionDto> options, BigDecimal principal) {
        if (options == null || options.isEmpty()) {
            return null;
        }

        int targetTenure;
        if (principal.compareTo(new BigDecimal("5000.00")) <= 0) {
            targetTenure = 3;
        } else if (principal.compareTo(new BigDecimal("50000.00")) >= 0) {
            targetTenure = 12;
        } else {
            targetTenure = 6;
        }

        return options.stream()
                .filter(opt -> opt.getTenureMonths() == targetTenure)
                .findFirst()
                .orElse(options.get(0));
    }
}
