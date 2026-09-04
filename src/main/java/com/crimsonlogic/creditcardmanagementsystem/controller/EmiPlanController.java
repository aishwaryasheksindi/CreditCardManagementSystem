package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IEmiPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emi-plans")
public class EmiPlanController {

    private final IEmiPlanService emiPlanService;

    public EmiPlanController(IEmiPlanService emiPlanService) {
        this.emiPlanService = emiPlanService;
    }

    @PostMapping
    public ResponseEntity<EmiPlanResponseDto> createEmiPlan(@Valid @RequestBody EmiPlanRequestDto requestDto) {
        EmiPlanResponseDto created = emiPlanService.createEmiPlan(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{emiPlanId}")
    public ResponseEntity<EmiPlanResponseDto> getEmiPlanById(@PathVariable String emiPlanId) {
        return ResponseEntity.ok(emiPlanService.getEmiPlanById(emiPlanId));
    }

    @GetMapping
    public ResponseEntity<List<EmiPlanResponseDto>> getAllEmiPlans(
            @RequestParam(required = false) String transactionId) {
        if (transactionId != null && !transactionId.isBlank()) {
            return ResponseEntity.ok(emiPlanService.getEmiPlansByTransactionId(transactionId));
        }
        return ResponseEntity.ok(emiPlanService.getAllEmiPlans());
    }

    @PutMapping("/{emiPlanId}")
    public ResponseEntity<EmiPlanResponseDto> updateEmiPlan(@PathVariable String emiPlanId,
                                                            @Valid @RequestBody EmiPlanRequestDto requestDto) {
        return ResponseEntity.ok(emiPlanService.updateEmiPlan(emiPlanId, requestDto));
    }

    @DeleteMapping("/{emiPlanId}")
    public ResponseEntity<Void> deleteEmiPlan(@PathVariable String emiPlanId) {
        emiPlanService.deleteEmiPlan(emiPlanId);
        return ResponseEntity.noContent().build();
    }
}
