package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final ICardService cardService;

    public CardController(ICardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponseDto> addCard(
            @Valid @RequestBody CardRequestDto cardDto) {

        CardResponseDto savedCard = cardService.addCard(cardDto);

        return new ResponseEntity<>(
                savedCard,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> getCardById(
            @PathVariable String cardId) {

        CardResponseDto cardDto = cardService.getCardById(cardId);

        return ResponseEntity.ok(cardDto);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> updateCard(
            @PathVariable String cardId,
            @Valid @RequestBody CardRequestDto cardDto) {

        CardResponseDto updatedCard = cardService.updateCard(cardId, cardDto);

        return ResponseEntity.ok(updatedCard);
    }
}