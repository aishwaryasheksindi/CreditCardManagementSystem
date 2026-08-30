package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryDto;

public interface ITransactionCategoryService {

	TransactionCategoryDto addCategory(TransactionCategoryDto categoryDto);

	TransactionCategoryDto getCategoryById(String categoryId);
}