package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiFraudRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiFraudRiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/fraud-risk")
public class AiFraudRiskController {

    private final IAiFraudRiskService aiFraudRiskService;

    public AiFraudRiskController(IAiFraudRiskService aiFraudRiskService) {
        this.aiFraudRiskService = aiFraudRiskService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<AiFraudRiskResponseDto> getFraudRisk(@PathVariable String transactionId) {
        AiFraudRiskResponseDto response = aiFraudRiskService.getFraudRisk(transactionId);
        return ResponseEntity.ok(response);
    }
}
