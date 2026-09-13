package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiPaymentRiskResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiPaymentRiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/payment-risk")
public class AiPaymentRiskController {

    private final IAiPaymentRiskService aiPaymentRiskService;

    public AiPaymentRiskController(IAiPaymentRiskService aiPaymentRiskService) {
        this.aiPaymentRiskService = aiPaymentRiskService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<AiPaymentRiskResponseDto> getPaymentRisk(@PathVariable String customerId) {
        AiPaymentRiskResponseDto response = aiPaymentRiskService.getPaymentRisk(customerId);
        return ResponseEntity.ok(response);
    }
}
