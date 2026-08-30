package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionCategoryDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionCategoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategoryServiceImpl implements ITransactionCategoryService {

    private final TransactionCategoryRepository categoryRepository;
    private final IdGenerationUtil idGenerationUtil;

    public TransactionCategoryServiceImpl(TransactionCategoryRepository categoryRepository,
                               IdGenerationUtil idGenerationUtil) {
        this.categoryRepository = categoryRepository;
        this.idGenerationUtil = idGenerationUtil;
    }

    @Override
    public TransactionCategoryDto addCategory(TransactionCategoryDto categoryDto) {

        String categoryId;

        do {
            categoryId = idGenerationUtil.generateCategoryId();
        } while (categoryRepository.existsById(categoryId));

        TransactionCategory category = new TransactionCategory();

        category.setCategoryId(categoryId);
        category.setCategoryName(categoryDto.getCategoryName());
        category.setDescription(categoryDto.getDescription());

        TransactionCategory savedCategory = categoryRepository.save(category);

        return convertToDto(savedCategory);
    }

    @Override
    public TransactionCategoryDto getCategoryById(String categoryId) {

    	TransactionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with ID: " + categoryId
                        )
                );

        return convertToDto(category);
    }

    private TransactionCategoryDto convertToDto(TransactionCategory category) {

    	TransactionCategoryDto categoryDto = new TransactionCategoryDto();

        categoryDto.setCategoryId(category.getCategoryId());
        categoryDto.setCategoryName(category.getCategoryName());
        categoryDto.setDescription(category.getDescription());

        return categoryDto;
    }
}