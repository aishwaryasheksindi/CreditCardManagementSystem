package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ITransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDto> addTransaction(
            @Valid @RequestBody TransactionRequestDto transactionDto) {

        TransactionResponseDto savedTransaction =
                transactionService.addTransaction(transactionDto);

        return new ResponseEntity<>(
                savedTransaction,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @PathVariable String transactionId) {

        TransactionResponseDto transactionDto =
                transactionService.getTransactionById(transactionId);

        return ResponseEntity.ok(transactionDto);
    }
}