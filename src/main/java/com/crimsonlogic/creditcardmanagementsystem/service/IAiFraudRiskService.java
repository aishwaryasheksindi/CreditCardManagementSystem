package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiFraudRiskResponseDto;

public interface IAiFraudRiskService {

    AiFraudRiskResponseDto getFraudRisk(String transactionId);
}
