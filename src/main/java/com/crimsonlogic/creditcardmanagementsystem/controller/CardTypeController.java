package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardTypeRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardTypeResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardTypeService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card-types")
public class CardTypeController {

    @Autowired
    private ICardTypeService cardTypeService;

    // Add a new card type
    @PostMapping
    public ResponseEntity<CardTypeResponseDto> addCardType(
            @Valid @RequestBody CardTypeRequestDto cardTypeDto) {

        CardTypeResponseDto savedCardType =
                cardTypeService.addCardType(cardTypeDto);

        return ResponseEntity.ok(savedCardType);
    }

    // Get card type by ID
    @GetMapping("/{cardTypeId}")
    public ResponseEntity<CardTypeResponseDto> getCardTypeById(
            @PathVariable String cardTypeId) {

        CardTypeResponseDto cardTypeDto =
                cardTypeService.getCardTypeById(cardTypeId);

        return ResponseEntity.ok(cardTypeDto);
    }
}