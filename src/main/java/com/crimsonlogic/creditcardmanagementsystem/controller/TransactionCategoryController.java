package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ITransactionCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/categories", "/api/transaction-categories"})
public class TransactionCategoryController {

    private final ITransactionCategoryService categoryService;

    public TransactionCategoryController(ITransactionCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionCategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<TransactionCategoryResponseDto> addCategory(
            @Valid @RequestBody TransactionCategoryRequestDto categoryDto) {

    	TransactionCategoryResponseDto savedCategory =
                categoryService.addCategory(categoryDto);

        return new ResponseEntity<>(
                savedCategory,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<TransactionCategoryResponseDto> getCategoryById(
            @PathVariable String categoryId) {

    	TransactionCategoryResponseDto categoryDto =
                categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(categoryDto);
    }
}