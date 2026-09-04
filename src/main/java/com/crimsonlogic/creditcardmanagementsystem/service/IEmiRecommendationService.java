package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiRecommendationResponseDto;

import java.util.List;

public interface IEmiRecommendationService {

    EmiRecommendationResponseDto createRecommendation(EmiRecommendationRequestDto requestDto);

    EmiRecommendationResponseDto getRecommendationById(String recommendationId);

    List<EmiRecommendationResponseDto> getAllRecommendations();

    List<EmiRecommendationResponseDto> getRecommendationsByTransactionId(String transactionId);

    EmiRecommendationResponseDto updateRecommendation(String recommendationId, EmiRecommendationRequestDto requestDto);

    void deleteRecommendation(String recommendationId);
}
