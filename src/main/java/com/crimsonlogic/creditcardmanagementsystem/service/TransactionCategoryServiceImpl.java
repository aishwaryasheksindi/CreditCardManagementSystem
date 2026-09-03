package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionCategoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategoryServiceImpl implements ITransactionCategoryService {

    private final TransactionCategoryRepository categoryRepository;

    public TransactionCategoryServiceImpl(TransactionCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public TransactionCategoryResponseDto addCategory(TransactionCategoryRequestDto categoryDto) {

        String categoryId;

        do {
            categoryId = IdGenerationUtil.generateCategoryId();
        } while (categoryRepository.existsById(categoryId));

        TransactionCategory category = new TransactionCategory();

        category.setCategoryId(categoryId);
        category.setCategoryName(categoryDto.getCategoryName());
        category.setDescription(categoryDto.getDescription());

        TransactionCategory savedCategory = categoryRepository.save(category);

        return convertToResponseDto(savedCategory);
    }

    @Override
    public TransactionCategoryResponseDto getCategoryById(String categoryId) {

    	TransactionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + categoryId
                        )
                );

        return convertToResponseDto(category);
    }

    private TransactionCategoryResponseDto convertToResponseDto(TransactionCategory category) {

    	TransactionCategoryResponseDto categoryDto = new TransactionCategoryResponseDto();

        categoryDto.setCategoryId(category.getCategoryId());
        categoryDto.setCategoryName(category.getCategoryName());
        categoryDto.setDescription(category.getDescription());

        return categoryDto;
    }
}