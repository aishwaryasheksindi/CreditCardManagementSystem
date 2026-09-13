package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.AiEmiRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiTenureOptionDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.EmiRecommendation;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.EmiRecommendationRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.utility.EmiCalculatorUtil;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AiEmiRecommendationServiceImpl implements IAiEmiRecommendationService {

    private final TransactionRepository transactionRepository;
    private final EmiRecommendationRepository emiRecommendationRepository;
    private final GeminiApiClient geminiApiClient;
    private final CurrentUserContext currentUserContext;

    public AiEmiRecommendationServiceImpl(TransactionRepository transactionRepository,
                                          EmiRecommendationRepository emiRecommendationRepository,
                                          GeminiApiClient geminiApiClient,
                                          CurrentUserContext currentUserContext) {
        this.transactionRepository = transactionRepository;
        this.emiRecommendationRepository = emiRecommendationRepository;
        this.geminiApiClient = geminiApiClient;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public AiEmiRecommendationResponseDto getEmiRecommendation(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        Card card = transaction.getCard();
        if (card == null) {
            throw new IllegalArgumentException("Transaction does not have an associated card");
        }

        Customer customer = card.getCustomer();
        if (customer == null) {
            throw new IllegalArgumentException("Card does not have an associated customer");
        }

        // Ownership enforcement: Ensure logged-in customer owns this card/transaction
        currentUserContext.assertCustomerOwnership(customer.getCustomerId());

        // Validate Card Status
        if (card.getCardStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Card is not active for EMI conversion. Current card status: " + card.getCardStatus());
        }

        // Validate Card Type EMI eligibility
        if (card.getCardType() != null && Boolean.FALSE.equals(card.getCardType().getEmiEligible())) {
            throw new IllegalArgumentException("This card type is not eligible for EMI conversion");
        }

        // Validate Transaction Type
        if (transaction.getTransactionType() == TransactionType.EMI) {
            throw new IllegalArgumentException("Transaction is already an EMI conversion");
        }

        // Validate Minimum Transaction Amount Rule (>= ₹3000)
        BigDecimal amount = transaction.getAmount();
        if (amount == null || amount.compareTo(EmiCalculatorUtil.MIN_EMI_AMOUNT) < 0) {
            throw new IllegalArgumentException("Transaction amount must be at least ₹3000 to be eligible for EMI conversion");
        }

        // Determine Applicable Annual Interest Rate
        BigDecimal annualInterestRate = determineInterestRate(card);

        // Deterministic Financial Calculations for all supported tenures
        List<EmiTenureOptionDto> options = EmiCalculatorUtil.calculateAllTenureOptions(amount, annualInterestRate);

        // Consult AI (Gemini) for tenure selection and customer-friendly explanation
        Optional<GeminiApiClient.AiRecommendationResult> aiResultOpt =
                geminiApiClient.getEmiRecommendation(amount, card.getAvailableLimit(), options);

        EmiTenureOptionDto chosenOption;
        String reason;
        boolean aiGenerated;

        if (aiResultOpt.isPresent()) {
            GeminiApiClient.AiRecommendationResult aiResult = aiResultOpt.get();
            Optional<EmiTenureOptionDto> matched = options.stream()
                    .filter(opt -> opt.getTenureMonths().equals(aiResult.getRecommendedTenureMonths()))
                    .findFirst();

            if (matched.isPresent()) {
                chosenOption = matched.get();
                reason = aiResult.getExplanation();
                aiGenerated = true;
            } else {
                // AI suggested an invalid/unsupported tenure, fall back deterministically
                chosenOption = EmiCalculatorUtil.selectDefaultRecommendation(options, amount);
                reason = String.format("A %d-month tenure offers a balanced monthly installment of ₹%s with an affordable total interest of ₹%s.",
                        chosenOption.getTenureMonths(), chosenOption.getMonthlyEmi(), chosenOption.getTotalInterest());
                aiGenerated = false;
            }
        } else {
            // Graceful fallback when AI is unavailable or unconfigured
            chosenOption = EmiCalculatorUtil.selectDefaultRecommendation(options, amount);
            reason = String.format("A %d-month tenure offers a balanced monthly installment of ₹%s with an affordable total interest of ₹%s.",
                    chosenOption.getTenureMonths(), chosenOption.getMonthlyEmi(), chosenOption.getTotalInterest());
            aiGenerated = false;
        }

        // Persist or update the recommendation entity for historical tracking
        persistRecommendation(transactionId, chosenOption);

        // Sanitize card reference (show only last 4 chars)
        String cardRef = card.getCardReference() != null ? card.getCardReference() : card.getCardId();
        String cardLast4 = cardRef.length() >= 4 ? cardRef.substring(cardRef.length() - 4) : cardRef;

        return new AiEmiRecommendationResponseDto(
                transactionId,
                amount,
                cardLast4,
                true,
                "Transaction meets minimum amount criteria (≥ ₹3000) and card is active.",
                chosenOption.getTenureMonths(),
                chosenOption.getMonthlyEmi(),
                chosenOption.getTotalInterest(),
                chosenOption.getTotalPayable(),
                chosenOption.getProcessingFee(),
                annualInterestRate,
                reason,
                aiGenerated,
                options
        );
    }

    private BigDecimal determineInterestRate(Card card) {
        if (card.getInterestRate() != null && card.getInterestRate().compareTo(BigDecimal.ZERO) > 0) {
            return card.getInterestRate();
        }
        if (card.getCardType() != null && card.getCardType().getInterestRate() != null
                && card.getCardType().getInterestRate().compareTo(BigDecimal.ZERO) > 0) {
            return card.getCardType().getInterestRate();
        }
        return EmiCalculatorUtil.DEFAULT_ANNUAL_INTEREST_RATE;
    }

    private void persistRecommendation(String transactionId, EmiTenureOptionDto chosenOption) {
        List<EmiRecommendation> existingList = emiRecommendationRepository.findByTransactionId(transactionId);
        EmiRecommendation rec;
        if (!existingList.isEmpty()) {
            rec = existingList.get(0);
        } else {
            rec = new EmiRecommendation();
            rec.setRecommendationId(IdGenerationUtil.generateEmiRecommendationId());
            rec.setTransactionId(transactionId);
        }
        rec.setTenureMonths(chosenOption.getTenureMonths());
        rec.setTotalPayable(chosenOption.getTotalPayable());
        rec.setFees(chosenOption.getProcessingFee());
        emiRecommendationRepository.save(rec);
    }
}
