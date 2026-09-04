package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IFraudAlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-alerts")
public class FraudAlertController {

    private final IFraudAlertService fraudAlertService;

    public FraudAlertController(IFraudAlertService fraudAlertService) {
        this.fraudAlertService = fraudAlertService;
    }

    @PostMapping
    public ResponseEntity<FraudAlertResponseDto> createFraudAlert(@Valid @RequestBody FraudAlertRequestDto requestDto) {
        FraudAlertResponseDto created = fraudAlertService.createFraudAlert(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{fraudAlertId}")
    public ResponseEntity<FraudAlertResponseDto> getFraudAlertById(@PathVariable String fraudAlertId) {
        return ResponseEntity.ok(fraudAlertService.getFraudAlertById(fraudAlertId));
    }

    @GetMapping
    public ResponseEntity<List<FraudAlertResponseDto>> getAllFraudAlerts(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String status) {
        if (transactionId != null && !transactionId.isBlank()) {
            return ResponseEntity.ok(fraudAlertService.getFraudAlertsByTransactionId(transactionId));
        }
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(fraudAlertService.getFraudAlertsByStatus(status));
        }
        return ResponseEntity.ok(fraudAlertService.getAllFraudAlerts());
    }

    @PutMapping("/{fraudAlertId}")
    public ResponseEntity<FraudAlertResponseDto> updateFraudAlert(@PathVariable String fraudAlertId,
                                                                  @Valid @RequestBody FraudAlertRequestDto requestDto) {
        return ResponseEntity.ok(fraudAlertService.updateFraudAlert(fraudAlertId, requestDto));
    }

    @DeleteMapping("/{fraudAlertId}")
    public ResponseEntity<Void> deleteFraudAlert(@PathVariable String fraudAlertId) {
        fraudAlertService.deleteFraudAlert(fraudAlertId);
        return ResponseEntity.noContent().build();
    }
}
