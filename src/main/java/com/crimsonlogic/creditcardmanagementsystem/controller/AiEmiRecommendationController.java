package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiEmiRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiEmiRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/emi-recommendation")
public class AiEmiRecommendationController {

    private final IAiEmiRecommendationService aiEmiRecommendationService;

    public AiEmiRecommendationController(IAiEmiRecommendationService aiEmiRecommendationService) {
        this.aiEmiRecommendationService = aiEmiRecommendationService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<AiEmiRecommendationResponseDto> getEmiRecommendation(@PathVariable String transactionId) {
        AiEmiRecommendationResponseDto response = aiEmiRecommendationService.getEmiRecommendation(transactionId);
        return ResponseEntity.ok(response);
    }
}
