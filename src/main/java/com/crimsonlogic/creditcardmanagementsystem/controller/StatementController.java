package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IStatementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final IStatementService statementService;

    public StatementController(IStatementService statementService) {
        this.statementService = statementService;
    }

    @PostMapping
    public ResponseEntity<StatementResponseDto> addStatement(
            @Valid @RequestBody StatementRequestDto statementDto) {
        StatementResponseDto savedStatement = statementService.addStatement(statementDto);
        return new ResponseEntity<>(savedStatement, HttpStatus.CREATED);
    }

    @GetMapping("/{statementId}")
    public ResponseEntity<StatementResponseDto> getStatementById(
            @PathVariable String statementId) {
        StatementResponseDto statementDto = statementService.getStatementById(statementId);
        return ResponseEntity.ok(statementDto);
    }

    @GetMapping
    public ResponseEntity<List<StatementResponseDto>> getAllStatements() {
        List<StatementResponseDto> statements = statementService.getAllStatements();
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<StatementResponseDto>> getStatementsByCardId(
            @PathVariable String cardId) {
        List<StatementResponseDto> statements = statementService.getStatementsByCardId(cardId);
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/{statementId}/download")
    public ResponseEntity<StatementResponseDto> downloadStatement(
            @PathVariable String statementId) {
        StatementResponseDto statementDto = statementService.getStatementById(statementId);
        return ResponseEntity.ok(statementDto);
    }
}
