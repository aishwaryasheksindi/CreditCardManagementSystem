package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiSpendingAnalysisResponseDto;

public interface IAiSpendingAnalysisService {

    AiSpendingAnalysisResponseDto getSpendingAnalysis(String customerId);
}
