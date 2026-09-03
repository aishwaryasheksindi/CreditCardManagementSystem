package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.MerchantRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.MerchantResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IMerchantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final IMerchantService merchantService;

    public MerchantController(IMerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping
    public ResponseEntity<MerchantResponseDto> addMerchant(
            @Valid @RequestBody MerchantRequestDto merchantDto) {

        MerchantResponseDto savedMerchant =
                merchantService.addMerchant(merchantDto);

        return new ResponseEntity<>(
                savedMerchant,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantResponseDto> getMerchantById(
            @PathVariable String merchantId) {

        MerchantResponseDto merchantDto =
                merchantService.getMerchantById(merchantId);

        return ResponseEntity.ok(merchantDto);
    }
}