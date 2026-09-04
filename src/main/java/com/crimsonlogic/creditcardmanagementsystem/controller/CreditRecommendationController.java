package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CreditRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CreditRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ICreditRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-recommendations")
public class CreditRecommendationController {

    private final ICreditRecommendationService creditRecommendationService;

    public CreditRecommendationController(ICreditRecommendationService creditRecommendationService) {
        this.creditRecommendationService = creditRecommendationService;
    }

    @PostMapping
    public ResponseEntity<CreditRecommendationResponseDto> createRecommendation(@Valid @RequestBody CreditRecommendationRequestDto requestDto) {
        CreditRecommendationResponseDto created = creditRecommendationService.createRecommendation(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<CreditRecommendationResponseDto> getRecommendationById(@PathVariable String recommendationId) {
        return ResponseEntity.ok(creditRecommendationService.getRecommendationById(recommendationId));
    }

    @GetMapping
    public ResponseEntity<List<CreditRecommendationResponseDto>> getAllRecommendations(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(creditRecommendationService.getRecommendationsByCustomerId(customerId));
        }
        return ResponseEntity.ok(creditRecommendationService.getAllRecommendations());
    }

    @PutMapping("/{recommendationId}")
    public ResponseEntity<CreditRecommendationResponseDto> updateRecommendation(@PathVariable String recommendationId,
                                                                                @Valid @RequestBody CreditRecommendationRequestDto requestDto) {
        return ResponseEntity.ok(creditRecommendationService.updateRecommendation(recommendationId, requestDto));
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable String recommendationId) {
        creditRecommendationService.deleteRecommendation(recommendationId);
        return ResponseEntity.noContent().build();
    }
}
