package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AnomalyDetectionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.AnomalyFlag;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.AnomalyType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnomalyDetectionServiceImpl implements IAnomalyDetectionService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final CurrentUserContext currentUserContext;

    public AnomalyDetectionServiceImpl(CustomerRepository customerRepository,
                                       TransactionRepository transactionRepository,
                                       CardRepository cardRepository,
                                       CurrentUserContext currentUserContext) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public AnomalyDetectionResponseDto detectAnomalies(String customerId) {
        // 1. Validate Customer
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        // 2. Ownership enforcement (Staff bypasses, Customer must match ID)
        currentUserContext.assertCustomerOwnership(customerId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime ninetyDaysAgo = now.minusDays(90);

        // 3. Retrieve Customer Transactions & Cards
        List<Transaction> allCustomerTxns = transactionRepository
                .findByCard_Customer_CustomerIdOrderByTransactionDateDesc(customerId);
        List<Card> cards = cardRepository.findByCustomer_CustomerId(customerId);

        // Partition into recent window (last 7 days) and historical comparison window (days 8 to 90)
        List<Transaction> recentTxns = allCustomerTxns.stream()
                .filter(t -> t.getTransactionDate() != null && !t.getTransactionDate().isBefore(sevenDaysAgo))
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .toList();

        List<Transaction> historicalTxns = allCustomerTxns.stream()
                .filter(t -> t.getTransactionDate() != null
                        && t.getTransactionDate().isBefore(sevenDaysAgo)
                        && !t.getTransactionDate().isBefore(ninetyDaysAgo))
                .toList();

        List<AnomalyFlag> flags = new ArrayList<>();

        // Anomaly Rule 1: Spending Spikes
        detectSpendingSpikes(recentTxns, historicalTxns, now, flags);

        // Anomaly Rule 2: Unusual Merchants
        detectUnusualMerchants(recentTxns, historicalTxns, now, flags);

        // Anomaly Rule 3: Unusual Location
        detectUnusualLocations(recentTxns, historicalTxns, now, flags);

        // Anomaly Rule 4: Unusual Time
        detectUnusualTime(recentTxns, historicalTxns, now, flags);

        // Anomaly Rule 5: Rapid Transactions
        detectRapidTransactions(recentTxns, now, flags);

        // Anomaly Rule 6: New Spending Category
        detectNewCategories(recentTxns, historicalTxns, now, flags);

        // Anomaly Rule 7: Repeated Failed Attempts (Card Level)
        detectRepeatedFailedAttempts(cards, now, flags);

        // Informational Review Alerts Only: Read-only, no cards blocked, no fraud alerts created
        return new AnomalyDetectionResponseDto(customerId, now, flags);
    }

    private void detectSpendingSpikes(List<Transaction> recentTxns, List<Transaction> historicalTxns, LocalDateTime now, List<AnomalyFlag> flags) {
        for (Transaction t : recentTxns) {
            if (t.getCategory() == null || t.getCategory().getCategoryName() == null || t.getAmount() == null) {
                continue;
            }
            String categoryName = t.getCategory().getCategoryName();

            List<Transaction> histCatTxns = historicalTxns.stream()
                    .filter(h -> h.getCategory() != null && categoryName.equalsIgnoreCase(h.getCategory().getCategoryName()))
                    .filter(h -> h.getAmount() != null)
                    .toList();

            if (!histCatTxns.isEmpty()) {
                BigDecimal sum = histCatTxns.stream()
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal avg = sum.divide(BigDecimal.valueOf(histCatTxns.size()), 2, RoundingMode.HALF_UP);

                if (avg.compareTo(BigDecimal.ZERO) > 0) {
                    double ratio = t.getAmount().divide(avg, 2, RoundingMode.HALF_UP).doubleValue();
                    if (ratio >= 2.5) {
                        String severity = ratio >= 4.0 ? "HIGH" : "MEDIUM";
                        String desc = String.format("Recent transaction amount ₹%s is %.1fx higher than the historical category average of ₹%s for '%s'.",
                                t.getAmount(), ratio, avg, categoryName);
                        flags.add(new AnomalyFlag(AnomalyType.SPENDING_SPIKE, desc, t.getTransactionId(), severity, now));
                    }
                }
            }
        }
    }

    private void detectUnusualMerchants(List<Transaction> recentTxns, List<Transaction> historicalTxns, LocalDateTime now, List<AnomalyFlag> flags) {
        if (historicalTxns.isEmpty()) {
            return;
        }

        Set<String> flaggedCategoriesInRecent = new HashSet<>();
        for (Transaction t : recentTxns) {
            if (t.getMerchant() == null || t.getMerchant().getMerchantCategory() == null) {
                continue;
            }
            String mCat = t.getMerchant().getMerchantCategory().trim();
            if (flaggedCategoriesInRecent.contains(mCat.toLowerCase())) {
                continue;
            }

            long histCount = historicalTxns.stream()
                    .filter(h -> h.getMerchant() != null && h.getMerchant().getMerchantCategory() != null)
                    .filter(h -> mCat.equalsIgnoreCase(h.getMerchant().getMerchantCategory().trim()))
                    .count();

            String mName = t.getMerchant().getMerchantName() != null ? t.getMerchant().getMerchantName() : "Merchant";

            if (histCount == 0) {
                flaggedCategoriesInRecent.add(mCat.toLowerCase());
                String desc = String.format("Transaction at '%s' belongs to merchant category '%s' which the customer has never transacted with historically.",
                        mName, mCat);
                flags.add(new AnomalyFlag(AnomalyType.UNUSUAL_MERCHANT, desc, t.getTransactionId(), "MEDIUM", now));
            } else if (histCount == 1 && historicalTxns.size() >= 5) {
                flaggedCategoriesInRecent.add(mCat.toLowerCase());
                String desc = String.format("Transaction at '%s' belongs to merchant category '%s' which the customer has rarely transacted with (only 1 historical transaction).",
                        mName, mCat);
                flags.add(new AnomalyFlag(AnomalyType.UNUSUAL_MERCHANT, desc, t.getTransactionId(), "LOW", now));
            }
        }
    }

    private void detectUnusualLocations(List<Transaction> recentTxns, List<Transaction> historicalTxns, LocalDateTime now, List<AnomalyFlag> flags) {
        Set<String> historicalLocations = historicalTxns.stream()
                .map(Transaction::getTransactionLocation)
                .filter(loc -> loc != null && !loc.isBlank())
                .map(loc -> loc.trim().toLowerCase())
                .collect(Collectors.toSet());

        if (historicalLocations.isEmpty()) {
            return;
        }

        for (Transaction t : recentTxns) {
            String loc = t.getTransactionLocation();
            if (loc != null && !loc.isBlank() && !historicalLocations.contains(loc.trim().toLowerCase())) {
                String desc = String.format("Transaction location '%s' differs from customer's typical historical locations.", loc.trim());
                flags.add(new AnomalyFlag(AnomalyType.UNUSUAL_LOCATION, desc, t.getTransactionId(), "MEDIUM", now));
            }
        }
    }

    private void detectUnusualTime(List<Transaction> recentTxns, List<Transaction> historicalTxns, LocalDateTime now, List<AnomalyFlag> flags) {
        if (historicalTxns.isEmpty()) {
            return;
        }

        boolean hasLateNightHistory = historicalTxns.stream()
                .filter(h -> h.getTransactionDate() != null)
                .anyMatch(h -> h.getTransactionDate().getHour() < 6);

        if (!hasLateNightHistory) {
            for (Transaction t : recentTxns) {
                if (t.getTransactionDate() != null && t.getTransactionDate().getHour() < 6) {
                    String desc = String.format("Transaction occurred at %02d:%02d, outside the customer's typical daytime activity hours.",
                            t.getTransactionDate().getHour(), t.getTransactionDate().getMinute());
                    flags.add(new AnomalyFlag(AnomalyType.UNUSUAL_TIME, desc, t.getTransactionId(), "LOW", now));
                }
            }
        }
    }

    private void detectRapidTransactions(List<Transaction> recentTxns, LocalDateTime now, List<AnomalyFlag> flags) {
        if (recentTxns.size() < 2) {
            return;
        }

        int i = 0;
        while (i < recentTxns.size() - 1) {
            LocalDateTime currentTxnTime = recentTxns.get(i).getTransactionDate();
            if (currentTxnTime == null) {
                i++;
                continue;
            }

            // Check how many transactions fall within 15 minutes of currentTxnTime
            int burstCount = 1;
            int j = i + 1;
            while (j < recentTxns.size()) {
                LocalDateTime nextTime = recentTxns.get(j).getTransactionDate();
                if (nextTime != null && Duration.between(currentTxnTime, nextTime).toMinutes() <= 15) {
                    burstCount++;
                    j++;
                } else {
                    break;
                }
            }

            if (burstCount >= 3) {
                Transaction lastInBurst = recentTxns.get(j - 1);
                String desc = String.format("Rapid transaction burst detected: %d transactions occurred within a 15-minute window.", burstCount);
                flags.add(new AnomalyFlag(AnomalyType.RAPID_TRANSACTIONS, desc, lastInBurst.getTransactionId(), "HIGH", now));
                i = j;
            } else {
                // Check if 2 transactions occurred within 5 minutes
                LocalDateTime nextTime = recentTxns.get(i + 1).getTransactionDate();
                if (nextTime != null && Duration.between(currentTxnTime, nextTime).toMinutes() <= 5) {
                    Transaction t2 = recentTxns.get(i + 1);
                    long mins = Duration.between(currentTxnTime, nextTime).toMinutes();
                    String desc = String.format("Rapid consecutive transactions detected: 2 transactions occurred within %d minute(s).", Math.max(1, mins));
                    flags.add(new AnomalyFlag(AnomalyType.RAPID_TRANSACTIONS, desc, t2.getTransactionId(), "MEDIUM", now));
                    i += 2;
                } else {
                    i++;
                }
            }
        }
    }

    private void detectNewCategories(List<Transaction> recentTxns, List<Transaction> historicalTxns, LocalDateTime now, List<AnomalyFlag> flags) {
        if (historicalTxns.isEmpty()) {
            return;
        }

        Set<String> historicalCategories = historicalTxns.stream()
                .filter(h -> h.getCategory() != null && h.getCategory().getCategoryName() != null)
                .map(h -> h.getCategory().getCategoryName().trim().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> flaggedCategories = new HashSet<>();
        for (Transaction t : recentTxns) {
            if (t.getCategory() == null || t.getCategory().getCategoryName() == null) {
                continue;
            }
            String catName = t.getCategory().getCategoryName().trim();
            if (!historicalCategories.contains(catName.toLowerCase()) && !flaggedCategories.contains(catName.toLowerCase())) {
                flaggedCategories.add(catName.toLowerCase());
                String desc = String.format("First-ever transaction recorded in spending category '%s'.", catName);
                flags.add(new AnomalyFlag(AnomalyType.NEW_CATEGORY, desc, t.getTransactionId(), "LOW", now));
            }
        }
    }

    private void detectRepeatedFailedAttempts(List<Card> cards, LocalDateTime now, List<AnomalyFlag> flags) {
        if (cards == null) {
            return;
        }
        for (Card card : cards) {
            int failedAttempts = card.getFailedPinAttempts();
            if (failedAttempts >= 2) {
                String severity = failedAttempts >= 3 ? "HIGH" : "MEDIUM";
                String cardRef = card.getCardReference() != null ? card.getCardReference() : card.getCardId();
                String desc = String.format("Card %s has %d repeated failed PIN attempts.", cardRef, failedAttempts);
                flags.add(new AnomalyFlag(AnomalyType.REPEATED_FAILED_ATTEMPTS, desc, null, severity, now));
            }
        }
    }
}
