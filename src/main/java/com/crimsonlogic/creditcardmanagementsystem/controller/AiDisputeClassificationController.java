package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAiDisputeClassificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/dispute-classification")
public class AiDisputeClassificationController {

    private final IAiDisputeClassificationService aiDisputeClassificationService;

    public AiDisputeClassificationController(IAiDisputeClassificationService aiDisputeClassificationService) {
        this.aiDisputeClassificationService = aiDisputeClassificationService;
    }

    @PostMapping
    public ResponseEntity<DisputeClassificationResponseDto> classifyDispute(
            @Valid @RequestBody DisputeClassificationRequestDto requestDto) {
        DisputeClassificationResponseDto response = aiDisputeClassificationService.classifyDispute(requestDto);
        return ResponseEntity.ok(response);
    }
}
