package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ISpendingInsightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spending-insights")
public class SpendingInsightController {

    private final ISpendingInsightService spendingInsightService;

    public SpendingInsightController(ISpendingInsightService spendingInsightService) {
        this.spendingInsightService = spendingInsightService;
    }

    @PostMapping
    public ResponseEntity<SpendingInsightResponseDto> createInsight(@Valid @RequestBody SpendingInsightRequestDto requestDto) {
        SpendingInsightResponseDto created = spendingInsightService.createInsight(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{insightId}")
    public ResponseEntity<SpendingInsightResponseDto> getInsightById(@PathVariable String insightId) {
        return ResponseEntity.ok(spendingInsightService.getInsightById(insightId));
    }

    @GetMapping
    public ResponseEntity<List<SpendingInsightResponseDto>> getAllInsights(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(spendingInsightService.getInsightsByCustomerId(customerId));
        }
        return ResponseEntity.ok(spendingInsightService.getAllInsights());
    }

    @PutMapping("/{insightId}")
    public ResponseEntity<SpendingInsightResponseDto> updateInsight(@PathVariable String insightId,
                                                                    @Valid @RequestBody SpendingInsightRequestDto requestDto) {
        return ResponseEntity.ok(spendingInsightService.updateInsight(insightId, requestDto));
    }

    @DeleteMapping("/{insightId}")
    public ResponseEntity<Void> deleteInsight(@PathVariable String insightId) {
        spendingInsightService.deleteInsight(insightId);
        return ResponseEntity.noContent().build();
    }
}
