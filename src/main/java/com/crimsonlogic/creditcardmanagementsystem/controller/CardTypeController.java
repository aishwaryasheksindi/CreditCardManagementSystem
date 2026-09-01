package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardTypeDto;
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
    public ResponseEntity<CardTypeDto> addCardType(
            @Valid @RequestBody CardTypeDto cardTypeDto) {

        CardTypeDto savedCardType =
                cardTypeService.addCardType(cardTypeDto);

        return ResponseEntity.ok(savedCardType);
    }

    // Get card type by ID
    @GetMapping("/{cardTypeId}")
    public ResponseEntity<CardTypeDto> getCardTypeById(
            @PathVariable String cardTypeId) {

        CardTypeDto cardTypeDto =
                cardTypeService.getCardTypeById(cardTypeId);

        return ResponseEntity.ok(cardTypeDto);
    }
}