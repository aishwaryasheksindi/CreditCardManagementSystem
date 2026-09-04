package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertResponseDto;

import java.util.List;

public interface IFraudAlertService {

    FraudAlertResponseDto createFraudAlert(FraudAlertRequestDto requestDto);

    FraudAlertResponseDto getFraudAlertById(String fraudAlertId);

    List<FraudAlertResponseDto> getAllFraudAlerts();

    List<FraudAlertResponseDto> getFraudAlertsByTransactionId(String transactionId);

    List<FraudAlertResponseDto> getFraudAlertsByStatus(String status);

    FraudAlertResponseDto updateFraudAlert(String fraudAlertId, FraudAlertRequestDto requestDto);

    void deleteFraudAlert(String fraudAlertId);
}
