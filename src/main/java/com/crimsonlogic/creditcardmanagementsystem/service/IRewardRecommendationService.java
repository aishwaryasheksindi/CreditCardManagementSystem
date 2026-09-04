package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationResponseDto;

import java.util.List;

public interface IRewardRecommendationService {

    RewardRecommendationResponseDto createRecommendation(RewardRecommendationRequestDto requestDto);

    RewardRecommendationResponseDto getRecommendationById(String recommendationId);

    List<RewardRecommendationResponseDto> getAllRecommendations();

    List<RewardRecommendationResponseDto> getRecommendationsByCustomerId(String customerId);

    RewardRecommendationResponseDto updateRecommendation(String recommendationId, RewardRecommendationRequestDto requestDto);

    void deleteRecommendation(String recommendationId);
}
