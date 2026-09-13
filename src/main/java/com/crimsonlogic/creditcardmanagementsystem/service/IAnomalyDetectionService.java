package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AnomalyDetectionResponseDto;

public interface IAnomalyDetectionService {
    AnomalyDetectionResponseDto detectAnomalies(String customerId);
}
