package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.RiskScore;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RiskScoreRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RiskScoreServiceImpl implements IRiskScoreService {

    private final RiskScoreRepository riskScoreRepository;
    private final TransactionRepository transactionRepository;

    public RiskScoreServiceImpl(RiskScoreRepository riskScoreRepository,
                                TransactionRepository transactionRepository) {
        this.riskScoreRepository = riskScoreRepository;
        this.transactionRepository = transactionRepository;
    }

    private String generateUniqueRiskScoreId() {
        String id;
        do {
            id = IdGenerationUtil.generateRiskScoreId();
        } while (riskScoreRepository.existsById(id));
        return id;
    }

    private void validateTransactionExists(String transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + transactionId);
        }
    }

    private String deriveRiskLevel(int score) {
        if (score <= 30) {
            return "LOW";
        } else if (score <= 70) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }

    @Override
    public RiskScoreResponseDto createRiskScore(RiskScoreRequestDto requestDto) {
        validateTransactionExists(requestDto.getTransactionId());

        RiskScore riskScore = new RiskScore();
        riskScore.setRiskScoreId(generateUniqueRiskScoreId());
        riskScore.setTransactionId(requestDto.getTransactionId());
        riskScore.setScore(requestDto.getScore());
        riskScore.setRiskLevel(deriveRiskLevel(requestDto.getScore()));
        riskScore.setModelVersion(requestDto.getModelVersion());
        riskScore.setScoredAt(LocalDateTime.now());
        riskScore.setRiskFactors(requestDto.getRiskFactors());

        RiskScore saved = riskScoreRepository.save(riskScore);
        return convertToResponseDto(saved);
    }

    @Override
    public RiskScoreResponseDto getRiskScoreById(String riskScoreId) {
        RiskScore riskScore = riskScoreRepository.findById(riskScoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Risk score not found with ID: " + riskScoreId));
        return convertToResponseDto(riskScore);
    }

    @Override
    public List<RiskScoreResponseDto> getAllRiskScores() {
        return riskScoreRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskScoreResponseDto> getRiskScoresByTransactionId(String transactionId) {
        return riskScoreRepository.findByTransactionId(transactionId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskScoreResponseDto> getRiskScoresByRiskLevel(String riskLevel) {
        return riskScoreRepository.findByRiskLevel(riskLevel)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public RiskScoreResponseDto updateRiskScore(String riskScoreId, RiskScoreRequestDto requestDto) {
        RiskScore riskScore = riskScoreRepository.findById(riskScoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Risk score not found with ID: " + riskScoreId));

        if (!riskScore.getTransactionId().equals(requestDto.getTransactionId())) {
            validateTransactionExists(requestDto.getTransactionId());
            riskScore.setTransactionId(requestDto.getTransactionId());
        }

        riskScore.setScore(requestDto.getScore());
        riskScore.setRiskLevel(deriveRiskLevel(requestDto.getScore()));
        riskScore.setModelVersion(requestDto.getModelVersion());
        riskScore.setRiskFactors(requestDto.getRiskFactors());

        RiskScore updated = riskScoreRepository.save(riskScore);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteRiskScore(String riskScoreId) {
        RiskScore riskScore = riskScoreRepository.findById(riskScoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Risk score not found with ID: " + riskScoreId));
        riskScoreRepository.delete(riskScore);
    }

    private RiskScoreResponseDto convertToResponseDto(RiskScore riskScore) {
        return new RiskScoreResponseDto(
                riskScore.getRiskScoreId(),
                riskScore.getTransactionId(),
                riskScore.getScore(),
                riskScore.getRiskLevel(),
                riskScore.getModelVersion(),
                riskScore.getScoredAt(),
                riskScore.getRiskFactors()
        );
    }
}
