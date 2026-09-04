package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeUpdateRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IDisputeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    private final IDisputeService disputeService;

    public DisputeController(IDisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PostMapping
    public ResponseEntity<DisputeResponseDto> raiseDispute(@Valid @RequestBody DisputeRequestDto requestDto) {
        DisputeResponseDto created = disputeService.raiseDispute(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{disputeId}")
    public ResponseEntity<DisputeResponseDto> getDisputeById(@PathVariable String disputeId) {
        return ResponseEntity.ok(disputeService.getDisputeById(disputeId));
    }

    @GetMapping
    public ResponseEntity<List<DisputeResponseDto>> getAllDisputes(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String transactionId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(disputeService.getDisputesByCustomerId(customerId));
        }
        if (transactionId != null && !transactionId.isBlank()) {
            return ResponseEntity.ok(disputeService.getDisputesByTransactionId(transactionId));
        }
        return ResponseEntity.ok(disputeService.getAllDisputes());
    }

    @PutMapping("/{disputeId}")
    public ResponseEntity<DisputeResponseDto> updateDispute(
            @PathVariable String disputeId,
            @Valid @RequestBody DisputeUpdateRequestDto updateDto) {
        DisputeResponseDto updated = disputeService.updateDispute(disputeId, updateDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{disputeId}")
    public ResponseEntity<Void> deleteDispute(@PathVariable String disputeId) {
        disputeService.deleteDispute(disputeId);
        return ResponseEntity.noContent().build();
    }
}
