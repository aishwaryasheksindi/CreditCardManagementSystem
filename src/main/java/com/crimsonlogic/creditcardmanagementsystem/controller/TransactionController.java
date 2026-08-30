package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionDto;
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
    public ResponseEntity<TransactionDto> addTransaction(
            @Valid @RequestBody TransactionDto transactionDto) {

        TransactionDto savedTransaction =
                transactionService.addTransaction(transactionDto);

        return new ResponseEntity<>(
                savedTransaction,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(
            @PathVariable String transactionId) {

        TransactionDto transactionDto =
                transactionService.getTransactionById(transactionId);

        return ResponseEntity.ok(transactionDto);
    }
}