package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreResponseDto;

import java.util.List;

public interface IRiskScoreService {

    RiskScoreResponseDto createRiskScore(RiskScoreRequestDto requestDto);

    RiskScoreResponseDto getRiskScoreById(String riskScoreId);

    List<RiskScoreResponseDto> getAllRiskScores();

    List<RiskScoreResponseDto> getRiskScoresByTransactionId(String transactionId);

    List<RiskScoreResponseDto> getRiskScoresByRiskLevel(String riskLevel);

    RiskScoreResponseDto updateRiskScore(String riskScoreId, RiskScoreRequestDto requestDto);

    void deleteRiskScore(String riskScoreId);
}
