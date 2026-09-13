package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiSpendingAnalysisResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiSpendingAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/spending-analysis")
public class AiSpendingAnalysisController {

    private final IAiSpendingAnalysisService aiSpendingAnalysisService;

    public AiSpendingAnalysisController(IAiSpendingAnalysisService aiSpendingAnalysisService) {
        this.aiSpendingAnalysisService = aiSpendingAnalysisService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<AiSpendingAnalysisResponseDto> getSpendingAnalysis(@PathVariable String customerId) {
        AiSpendingAnalysisResponseDto response = aiSpendingAnalysisService.getSpendingAnalysis(customerId);
        return ResponseEntity.ok(response);
    }
}
