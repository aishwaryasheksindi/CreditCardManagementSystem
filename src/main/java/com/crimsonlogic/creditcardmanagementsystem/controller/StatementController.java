package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementDto;
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
    public ResponseEntity<StatementDto> addStatement(
            @Valid @RequestBody StatementDto statementDto) {
        StatementDto savedStatement = statementService.addStatement(statementDto);
        return new ResponseEntity<>(savedStatement, HttpStatus.CREATED);
    }

    @GetMapping("/{statementId}")
    public ResponseEntity<StatementDto> getStatementById(
            @PathVariable String statementId) {
        StatementDto statementDto = statementService.getStatementById(statementId);
        return ResponseEntity.ok(statementDto);
    }

    @GetMapping
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        List<StatementDto> statements = statementService.getAllStatements();
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<StatementDto>> getStatementsByCardId(
            @PathVariable String cardId) {
        List<StatementDto> statements = statementService.getStatementsByCardId(cardId);
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/{statementId}/download")
    public ResponseEntity<StatementDto> downloadStatement(
            @PathVariable String statementId) {
        StatementDto statementDto = statementService.getStatementById(statementId);
        return ResponseEntity.ok(statementDto);
    }
}
