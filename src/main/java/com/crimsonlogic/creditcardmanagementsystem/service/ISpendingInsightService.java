package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightResponseDto;

import java.util.List;

public interface ISpendingInsightService {

    SpendingInsightResponseDto createInsight(SpendingInsightRequestDto requestDto);

    SpendingInsightResponseDto getInsightById(String insightId);

    List<SpendingInsightResponseDto> getAllInsights();

    List<SpendingInsightResponseDto> getInsightsByCustomerId(String customerId);

    SpendingInsightResponseDto updateInsight(String insightId, SpendingInsightRequestDto requestDto);

    void deleteInsight(String insightId);
}
