package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiEmiRecommendationResponseDto;

public interface IAiEmiRecommendationService {

    AiEmiRecommendationResponseDto getEmiRecommendation(String transactionId);
}
