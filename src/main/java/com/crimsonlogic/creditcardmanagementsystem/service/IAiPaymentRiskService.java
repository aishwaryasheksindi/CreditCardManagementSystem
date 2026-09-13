package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiPaymentRiskResponseDto;

public interface IAiPaymentRiskService {

    AiPaymentRiskResponseDto getPaymentRisk(String customerId);
}
