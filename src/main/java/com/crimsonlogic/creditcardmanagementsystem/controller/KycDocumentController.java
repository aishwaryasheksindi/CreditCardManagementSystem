package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycRejectRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycVerifyRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IKycDocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kyc-documents")
public class KycDocumentController {

    private final IKycDocumentService kycDocumentService;

    public KycDocumentController(IKycDocumentService kycDocumentService) {
        this.kycDocumentService = kycDocumentService;
    }

    @PostMapping
    public ResponseEntity<KycDocumentResponseDto> submitDocument(@Valid @RequestBody KycDocumentRequestDto requestDto) {
        KycDocumentResponseDto created = kycDocumentService.submitDocument(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KycDocumentResponseDto> getDocumentById(@PathVariable String id) {
        return ResponseEntity.ok(kycDocumentService.getDocumentById(id));
    }

    @GetMapping
    public ResponseEntity<List<KycDocumentResponseDto>> getAllDocuments(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(kycDocumentService.getDocumentsByCustomerId(customerId));
        }
        return ResponseEntity.ok(kycDocumentService.getAllDocuments());
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<KycDocumentResponseDto> verifyDocument(
            @PathVariable String id,
            @RequestParam(required = false) String staffId,
            @RequestBody(required = false) KycVerifyRequestDto verifyRequest) {
        String finalStaffId = staffId;
        if (finalStaffId == null && verifyRequest != null) {
            finalStaffId = verifyRequest.getVerifiedByStaffId();
        }
        KycDocumentResponseDto verified = kycDocumentService.verifyDocument(id, finalStaffId);
        return ResponseEntity.ok(verified);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<KycDocumentResponseDto> rejectDocument(
            @PathVariable String id,
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) String reason,
            @RequestBody(required = false) KycRejectRequestDto rejectRequest) {
        String finalStaffId = staffId;
        String finalReason = reason;
        if (rejectRequest != null) {
            if (finalStaffId == null) {
                finalStaffId = rejectRequest.getVerifiedByStaffId();
            }
            if (finalReason == null) {
                finalReason = rejectRequest.getRejectionReason();
            }
        }
        KycDocumentResponseDto rejected = kycDocumentService.rejectDocument(id, finalStaffId, finalReason);
        return ResponseEntity.ok(rejected);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        kycDocumentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
