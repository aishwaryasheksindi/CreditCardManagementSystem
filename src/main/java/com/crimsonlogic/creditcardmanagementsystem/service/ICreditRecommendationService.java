package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CreditRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CreditRecommendationResponseDto;

import java.util.List;

public interface ICreditRecommendationService {

    CreditRecommendationResponseDto createRecommendation(CreditRecommendationRequestDto requestDto);

    CreditRecommendationResponseDto getRecommendationById(String recommendationId);

    List<CreditRecommendationResponseDto> getAllRecommendations();

    List<CreditRecommendationResponseDto> getRecommendationsByCustomerId(String customerId);

    CreditRecommendationResponseDto updateRecommendation(String recommendationId, CreditRecommendationRequestDto requestDto);

    void deleteRecommendation(String recommendationId);
}
