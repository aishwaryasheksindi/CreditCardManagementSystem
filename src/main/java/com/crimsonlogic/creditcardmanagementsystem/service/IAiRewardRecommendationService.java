package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiRewardRecommendationResponseDto;

public interface IAiRewardRecommendationService {

    AiRewardRecommendationResponseDto getRewardRecommendation(String customerId);
}
