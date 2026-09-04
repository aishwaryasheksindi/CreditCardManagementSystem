package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IEmiRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emi-recommendations")
public class EmiRecommendationController {

    private final IEmiRecommendationService emiRecommendationService;

    public EmiRecommendationController(IEmiRecommendationService emiRecommendationService) {
        this.emiRecommendationService = emiRecommendationService;
    }

    @PostMapping
    public ResponseEntity<EmiRecommendationResponseDto> createRecommendation(@Valid @RequestBody EmiRecommendationRequestDto requestDto) {
        EmiRecommendationResponseDto created = emiRecommendationService.createRecommendation(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<EmiRecommendationResponseDto> getRecommendationById(@PathVariable String recommendationId) {
        return ResponseEntity.ok(emiRecommendationService.getRecommendationById(recommendationId));
    }

    @GetMapping
    public ResponseEntity<List<EmiRecommendationResponseDto>> getAllRecommendations(
            @RequestParam(required = false) String transactionId) {
        if (transactionId != null && !transactionId.isBlank()) {
            return ResponseEntity.ok(emiRecommendationService.getRecommendationsByTransactionId(transactionId));
        }
        return ResponseEntity.ok(emiRecommendationService.getAllRecommendations());
    }

    @PutMapping("/{recommendationId}")
    public ResponseEntity<EmiRecommendationResponseDto> updateRecommendation(@PathVariable String recommendationId,
                                                                                @Valid @RequestBody EmiRecommendationRequestDto requestDto) {
        return ResponseEntity.ok(emiRecommendationService.updateRecommendation(recommendationId, requestDto));
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable String recommendationId) {
        emiRecommendationService.deleteRecommendation(recommendationId);
        return ResponseEntity.noContent().build();
    }
}
