package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IStatementItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statement-items")
public class StatementItemController {

    private final IStatementItemService statementItemService;

    public StatementItemController(IStatementItemService statementItemService) {
        this.statementItemService = statementItemService;
    }

    @PostMapping
    public ResponseEntity<StatementItemDto> addStatementItem(
            @Valid @RequestBody StatementItemDto statementItemDto) {
        StatementItemDto savedItem = statementItemService.addStatementItem(statementItemDto);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @GetMapping("/{statementItemId}")
    public ResponseEntity<StatementItemDto> getStatementItemById(
            @PathVariable String statementItemId) {
        StatementItemDto itemDto = statementItemService.getStatementItemById(statementItemId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<List<StatementItemDto>> getAllStatementItems() {
        List<StatementItemDto> items = statementItemService.getAllStatementItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/statement/{statementId}")
    public ResponseEntity<List<StatementItemDto>> getItemsByStatementId(
            @PathVariable String statementId) {
        List<StatementItemDto> items = statementItemService.getItemsByStatementId(statementId);
        return ResponseEntity.ok(items);
    }
}
