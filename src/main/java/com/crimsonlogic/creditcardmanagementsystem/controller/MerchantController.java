package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.MerchantDto;
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
    public ResponseEntity<MerchantDto> addMerchant(
            @Valid @RequestBody MerchantDto merchantDto) {

        MerchantDto savedMerchant =
                merchantService.addMerchant(merchantDto);

        return new ResponseEntity<>(
                savedMerchant,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantDto> getMerchantById(
            @PathVariable String merchantId) {

        MerchantDto merchantDto =
                merchantService.getMerchantById(merchantId);

        return ResponseEntity.ok(merchantDto);
    }
}