package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Statement;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatementServiceImpl implements IStatementService {

    private final StatementRepository statementRepository;
    private final CardRepository cardRepository;

    public StatementServiceImpl(StatementRepository statementRepository,
                                CardRepository cardRepository) {
        this.statementRepository = statementRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public StatementDto addStatement(StatementDto statementDto) {

        if (!cardRepository.existsById(statementDto.getCardId())) {
            throw new RuntimeException("Card not found with ID: " + statementDto.getCardId());
        }

        String statementId;
        do {
            statementId = IdGenerationUtil.generateStatementId();
        } while (statementRepository.existsById(statementId));

        BigDecimal openingBalance = statementDto.getOpeningBalance();
        BigDecimal totalPurchases = statementDto.getTotalPurchases();
        BigDecimal totalPayments = statementDto.getTotalPayments();
        BigDecimal totalRefunds = statementDto.getTotalRefunds();
        BigDecimal totalFees = statementDto.getTotalFees();
        BigDecimal totalInterest = statementDto.getTotalInterest();

        BigDecimal closingBalance = openingBalance
                .add(totalPurchases)
                .subtract(totalPayments)
                .subtract(totalRefunds)
                .add(totalFees)
                .add(totalInterest);

        BigDecimal minimumDue = closingBalance.multiply(new BigDecimal("0.05"));
        if (closingBalance.compareTo(BigDecimal.ZERO) > 0
                && minimumDue.compareTo(new BigDecimal("200")) < 0) {
            minimumDue = new BigDecimal("200");
        } else if (closingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            minimumDue = BigDecimal.ZERO;
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
        return convertToDto(savedStatement);
    }

    @Override
    public StatementDto getStatementById(String statementId) {
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new RuntimeException("Statement not found with ID: " + statementId));
        return convertToDto(statement);
    }

    @Override
    public List<StatementDto> getStatementsByCardId(String cardId) {
        return statementRepository.findByCardId(cardId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StatementDto> getAllStatements() {
        return statementRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private StatementDto convertToDto(Statement statement) {
        StatementDto dto = new StatementDto();
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
