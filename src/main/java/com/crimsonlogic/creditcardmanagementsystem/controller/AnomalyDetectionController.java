package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.AnomalyDetectionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAnomalyDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/anomaly-detection")
public class AnomalyDetectionController {

    private final IAnomalyDetectionService anomalyDetectionService;

    public AnomalyDetectionController(IAnomalyDetectionService anomalyDetectionService) {
        this.anomalyDetectionService = anomalyDetectionService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<AnomalyDetectionResponseDto> getAnomalies(@PathVariable String customerId) {
        AnomalyDetectionResponseDto response = anomalyDetectionService.detectAnomalies(customerId);
        return ResponseEntity.ok(response);
    }
}
