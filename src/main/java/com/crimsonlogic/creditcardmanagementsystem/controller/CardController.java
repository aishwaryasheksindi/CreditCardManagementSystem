package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardDto;
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
    public ResponseEntity<CardDto> addCard(
            @Valid @RequestBody CardDto cardDto) {

        CardDto savedCard = cardService.addCard(cardDto);

        return new ResponseEntity<>(
                savedCard,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDto> getCardById(
            @PathVariable String cardId) {

        CardDto cardDto = cardService.getCardById(cardId);

        return ResponseEntity.ok(cardDto);
    }
}