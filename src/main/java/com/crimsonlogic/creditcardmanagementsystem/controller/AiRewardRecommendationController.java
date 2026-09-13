package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.AiRewardRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiRewardRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/reward-recommendation")
public class AiRewardRecommendationController {

    private final IAiRewardRecommendationService aiRewardRecommendationService;

    public AiRewardRecommendationController(IAiRewardRecommendationService aiRewardRecommendationService) {
        this.aiRewardRecommendationService = aiRewardRecommendationService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<AiRewardRecommendationResponseDto> getRewardRecommendation(@PathVariable String customerId) {
        AiRewardRecommendationResponseDto response = aiRewardRecommendationService.getRewardRecommendation(customerId);
        return ResponseEntity.ok(response);
    }
}
