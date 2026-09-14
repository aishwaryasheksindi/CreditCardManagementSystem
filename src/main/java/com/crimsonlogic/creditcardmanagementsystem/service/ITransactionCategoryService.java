package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryResponseDto;

import java.util.List;

public interface ITransactionCategoryService {

	TransactionCategoryResponseDto addCategory(TransactionCategoryRequestDto categoryDto);

	TransactionCategoryResponseDto getCategoryById(String categoryId);

	List<TransactionCategoryResponseDto> getAllCategories();
}