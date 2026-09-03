package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemResponseDto;
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
    public ResponseEntity<StatementItemResponseDto> addStatementItem(
            @Valid @RequestBody StatementItemRequestDto statementItemDto) {
        StatementItemResponseDto savedItem = statementItemService.addStatementItem(statementItemDto);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @GetMapping("/{statementItemId}")
    public ResponseEntity<StatementItemResponseDto> getStatementItemById(
            @PathVariable String statementItemId) {
        StatementItemResponseDto itemDto = statementItemService.getStatementItemById(statementItemId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<List<StatementItemResponseDto>> getAllStatementItems() {
        List<StatementItemResponseDto> items = statementItemService.getAllStatementItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/statement/{statementId}")
    public ResponseEntity<List<StatementItemResponseDto>> getItemsByStatementId(
            @PathVariable String statementId) {
        List<StatementItemResponseDto> items = statementItemService.getItemsByStatementId(statementId);
        return ResponseEntity.ok(items);
    }
}
