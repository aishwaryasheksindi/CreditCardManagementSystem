package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RiskScoreResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IRiskScoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-scores")
public class RiskScoreController {

    private final IRiskScoreService riskScoreService;

    public RiskScoreController(IRiskScoreService riskScoreService) {
        this.riskScoreService = riskScoreService;
    }

    @PostMapping
    public ResponseEntity<RiskScoreResponseDto> createRiskScore(@Valid @RequestBody RiskScoreRequestDto requestDto) {
        RiskScoreResponseDto created = riskScoreService.createRiskScore(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{riskScoreId}")
    public ResponseEntity<RiskScoreResponseDto> getRiskScoreById(@PathVariable String riskScoreId) {
        return ResponseEntity.ok(riskScoreService.getRiskScoreById(riskScoreId));
    }

    @GetMapping
    public ResponseEntity<List<RiskScoreResponseDto>> getAllRiskScores(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String riskLevel) {
        if (transactionId != null && !transactionId.isBlank()) {
            return ResponseEntity.ok(riskScoreService.getRiskScoresByTransactionId(transactionId));
        }
        if (riskLevel != null && !riskLevel.isBlank()) {
            return ResponseEntity.ok(riskScoreService.getRiskScoresByRiskLevel(riskLevel));
        }
        return ResponseEntity.ok(riskScoreService.getAllRiskScores());
    }

    @PutMapping("/{riskScoreId}")
    public ResponseEntity<RiskScoreResponseDto> updateRiskScore(@PathVariable String riskScoreId,
                                                                @Valid @RequestBody RiskScoreRequestDto requestDto) {
        return ResponseEntity.ok(riskScoreService.updateRiskScore(riskScoreId, requestDto));
    }

    @DeleteMapping("/{riskScoreId}")
    public ResponseEntity<Void> deleteRiskScore(@PathVariable String riskScoreId) {
        riskScoreService.deleteRiskScore(riskScoreId);
        return ResponseEntity.noContent().build();
    }
}
