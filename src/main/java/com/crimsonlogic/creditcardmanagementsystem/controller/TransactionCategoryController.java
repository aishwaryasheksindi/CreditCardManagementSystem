package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ITransactionCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class TransactionCategoryController {

    private final ITransactionCategoryService categoryService;

    public TransactionCategoryController(ITransactionCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<TransactionCategoryDto> addCategory(
            @Valid @RequestBody TransactionCategoryDto categoryDto) {

    	TransactionCategoryDto savedCategory =
                categoryService.addCategory(categoryDto);

        return new ResponseEntity<>(
                savedCategory,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<TransactionCategoryDto> getCategoryById(
            @PathVariable String categoryId) {

    	TransactionCategoryDto categoryDto =
                categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(categoryDto);
    }
}