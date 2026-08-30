package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ICardStatusHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card-status-history")
public class CardStatusHistoryController {

    private final ICardStatusHistoryService cardStatusHistoryService;

    public CardStatusHistoryController(
            ICardStatusHistoryService cardStatusHistoryService) {

        this.cardStatusHistoryService = cardStatusHistoryService;
    }

    @PostMapping
    public ResponseEntity<CardStatusHistoryDto> addCardStatusHistory(
            @Valid @RequestBody CardStatusHistoryDto cardStatusHistoryDto) {

        CardStatusHistoryDto savedHistory =
                cardStatusHistoryService.addCardStatusHistory(
                        cardStatusHistoryDto
                );

        return new ResponseEntity<>(
                savedHistory,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{cardStatusHistoryId}")
    public ResponseEntity<CardStatusHistoryDto> getCardStatusHistoryById(
            @PathVariable String cardStatusHistoryId) {

        CardStatusHistoryDto cardStatusHistoryDto =
                cardStatusHistoryService.getCardStatusHistoryById(
                        cardStatusHistoryId
                );

        return ResponseEntity.ok(cardStatusHistoryDto);
    }
}