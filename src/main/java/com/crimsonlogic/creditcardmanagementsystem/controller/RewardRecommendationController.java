package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IRewardRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reward-recommendations")
public class RewardRecommendationController {

    private final IRewardRecommendationService rewardRecommendationService;

    public RewardRecommendationController(IRewardRecommendationService rewardRecommendationService) {
        this.rewardRecommendationService = rewardRecommendationService;
    }

    @PostMapping
    public ResponseEntity<RewardRecommendationResponseDto> createRecommendation(@Valid @RequestBody RewardRecommendationRequestDto requestDto) {
        RewardRecommendationResponseDto created = rewardRecommendationService.createRecommendation(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<RewardRecommendationResponseDto> getRecommendationById(@PathVariable String recommendationId) {
        return ResponseEntity.ok(rewardRecommendationService.getRecommendationById(recommendationId));
    }

    @GetMapping
    public ResponseEntity<List<RewardRecommendationResponseDto>> getAllRecommendations(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(rewardRecommendationService.getRecommendationsByCustomerId(customerId));
        }
        return ResponseEntity.ok(rewardRecommendationService.getAllRecommendations());
    }

    @PutMapping("/{recommendationId}")
    public ResponseEntity<RewardRecommendationResponseDto> updateRecommendation(@PathVariable String recommendationId,
                                                                                @Valid @RequestBody RewardRecommendationRequestDto requestDto) {
        return ResponseEntity.ok(rewardRecommendationService.updateRecommendation(recommendationId, requestDto));
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable String recommendationId) {
        rewardRecommendationService.deleteRecommendation(recommendationId);
        return ResponseEntity.noContent().build();
    }
}
