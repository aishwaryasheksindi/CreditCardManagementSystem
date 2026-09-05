package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Payment;
import com.crimsonlogic.creditcardmanagementsystem.entity.Statement;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.PaymentRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatementServiceImpl implements IStatementService {

    private final StatementRepository statementRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final CurrentUserContext currentUserContext;

    public StatementServiceImpl(StatementRepository statementRepository,
                                CardRepository cardRepository,
                                TransactionRepository transactionRepository,
                                PaymentRepository paymentRepository,
                                CurrentUserContext currentUserContext) {
        this.statementRepository = statementRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public StatementResponseDto addStatement(StatementRequestDto statementDto) {

        if (!cardRepository.existsById(statementDto.getCardId())) {
            throw new ResourceNotFoundException("Card not found with ID: " + statementDto.getCardId());
        }

        String statementId;
        do {
            statementId = IdGenerationUtil.generateStatementId();
        } while (statementRepository.existsById(statementId));

        // Rule #6: Prior statement rollover for openingBalance and cycleStart
        Optional<Statement> previousStatementOpt = statementRepository
                .findTopByCardIdOrderByStatementDateDesc(statementDto.getCardId());

        BigDecimal openingBalance;
        if (previousStatementOpt.isPresent()) {
            openingBalance = previousStatementOpt.get().getClosingBalance() != null
                    ? previousStatementOpt.get().getClosingBalance() : BigDecimal.ZERO;
        } else {
            openingBalance = statementDto.getOpeningBalance() != null
                    ? statementDto.getOpeningBalance() : BigDecimal.ZERO;
        }
        openingBalance = openingBalance.setScale(2, RoundingMode.HALF_UP);

        LocalDateTime cycleEnd = statementDto.getStatementDate().atTime(LocalTime.MAX);
        LocalDateTime cycleStart;
        if (statementDto.getCycleStartDate() != null) {
            cycleStart = statementDto.getCycleStartDate().atStartOfDay();
        } else if (previousStatementOpt.isPresent()) {
            cycleStart = previousStatementOpt.get().getStatementDate().plusDays(1).atStartOfDay();
        } else {
            cycleStart = statementDto.getStatementDate().minusMonths(1).atStartOfDay();
        }

        // Aggregate actual transactions for this card within the billing cycle
        List<Transaction> transactions = transactionRepository
                .findByCard_CardIdAndTransactionDateBetween(statementDto.getCardId(), cycleStart, cycleEnd);

        BigDecimal totalPurchases = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (Transaction txn : transactions) {
            if (txn.getTransactionStatus() != null && txn.getTransactionStatus() == TransactionStatus.FAILED) {
                continue;
            }
            BigDecimal amount = txn.getAmount() != null ? txn.getAmount() : BigDecimal.ZERO;
            if (txn.getTransactionType() == null) {
                totalPurchases = totalPurchases.add(amount);
            } else {
                switch (txn.getTransactionType()) {
                    case PURCHASE:
                    case CASH_WITHDRAWAL:
                    case EMI:
                        totalPurchases = totalPurchases.add(amount);
                        break;
                    case REFUND:
                    case REVERSAL:
                        totalRefunds = totalRefunds.add(amount);
                        break;
                    case FEE:
                        totalFees = totalFees.add(amount);
                        break;
                    case INTEREST:
                        totalInterest = totalInterest.add(amount);
                        break;
                    case PAYMENT:
                        break;
                }
            }
        }
        totalPurchases = totalPurchases.setScale(2, RoundingMode.HALF_UP);
        totalRefunds = totalRefunds.setScale(2, RoundingMode.HALF_UP);
        totalFees = totalFees.setScale(2, RoundingMode.HALF_UP);
        totalInterest = totalInterest.setScale(2, RoundingMode.HALF_UP);

        // Aggregate actual successful payments for this card within the billing cycle
        List<Payment> payments = paymentRepository
                .findByCardIdAndPaymentDateBetweenAndPaymentStatus(
                        statementDto.getCardId(), cycleStart, cycleEnd, PaymentStatus.SUCCESS);

        BigDecimal totalPayments = BigDecimal.ZERO;
        for (Payment payment : payments) {
            if (payment.getAmount() != null) {
                totalPayments = totalPayments.add(payment.getAmount());
            }
        }
        totalPayments = totalPayments.setScale(2, RoundingMode.HALF_UP);

        // Compute closing balance
        BigDecimal closingBalance = openingBalance
                .add(totalPurchases)
                .subtract(totalPayments)
                .subtract(totalRefunds)
                .add(totalFees)
                .add(totalInterest)
                .setScale(2, RoundingMode.HALF_UP);

        // Compute minimum due (Rule #5: 5% of closing balance, minimum ₹200)
        BigDecimal minimumDue = closingBalance.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        if (closingBalance.compareTo(BigDecimal.ZERO) > 0
                && minimumDue.compareTo(new BigDecimal("200.00")) < 0) {
            minimumDue = closingBalance.min(new BigDecimal("200.00")).setScale(2, RoundingMode.HALF_UP);
        } else if (closingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            minimumDue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        Statement statement = new Statement();
        statement.setStatementId(statementId);
        statement.setCardId(statementDto.getCardId());
        statement.setStatementDate(statementDto.getStatementDate());
        statement.setDueDate(statementDto.getDueDate());
        statement.setOpeningBalance(openingBalance);
        statement.setTotalPurchases(totalPurchases);
        statement.setTotalPayments(totalPayments);
        statement.setTotalRefunds(totalRefunds);
        statement.setTotalFees(totalFees);
        statement.setTotalInterest(totalInterest);
        statement.setClosingBalance(closingBalance);
        statement.setMinimumDue(minimumDue);

        Statement savedStatement = statementRepository.save(statement);
        return convertToResponseDto(savedStatement);
    }

    @Override
    public StatementResponseDto getStatementById(String statementId) {
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found with ID: " + statementId));

        cardRepository.findById(statement.getCardId()).ifPresent(card -> {
            if (card.getCustomer() != null) {
                currentUserContext.assertCustomerOwnership(card.getCustomer().getCustomerId());
            }
        });

        return convertToResponseDto(statement);
    }

    @Override
    public List<StatementResponseDto> getStatementsByCardId(String cardId) {
        cardRepository.findById(cardId).ifPresent(card -> {
            if (card.getCustomer() != null) {
                currentUserContext.assertCustomerOwnership(card.getCustomer().getCustomerId());
            }
        });

        return statementRepository.findByCardId(cardId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StatementResponseDto> getAllStatements() {
        return statementRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private StatementResponseDto convertToResponseDto(Statement statement) {
        StatementResponseDto dto = new StatementResponseDto();
        dto.setStatementId(statement.getStatementId());
        dto.setCardId(statement.getCardId());
        dto.setStatementDate(statement.getStatementDate());
        dto.setDueDate(statement.getDueDate());
        dto.setOpeningBalance(statement.getOpeningBalance());
        dto.setTotalPurchases(statement.getTotalPurchases());
        dto.setTotalPayments(statement.getTotalPayments());
        dto.setTotalRefunds(statement.getTotalRefunds());
        dto.setTotalFees(statement.getTotalFees());
        dto.setTotalInterest(statement.getTotalInterest());
        dto.setClosingBalance(statement.getClosingBalance());
        dto.setMinimumDue(statement.getMinimumDue());
        return dto;
    }
}
