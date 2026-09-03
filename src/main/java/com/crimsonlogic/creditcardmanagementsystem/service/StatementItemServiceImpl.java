package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.StatementItem;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementItemRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatementItemServiceImpl implements IStatementItemService {

    private final StatementItemRepository statementItemRepository;
    private final StatementRepository statementRepository;

    public StatementItemServiceImpl(StatementItemRepository statementItemRepository,
                                    StatementRepository statementRepository) {
        this.statementItemRepository = statementItemRepository;
        this.statementRepository = statementRepository;
    }

    @Override
    public StatementItemResponseDto addStatementItem(StatementItemRequestDto statementItemDto) {

        if (!statementRepository.existsById(statementItemDto.getStatementId())) {
            throw new ResourceNotFoundException("Statement not found with ID: " + statementItemDto.getStatementId());
        }

        String statementItemId;
        do {
            statementItemId = IdGenerationUtil.generateStatementItemId();
        } while (statementItemRepository.existsById(statementItemId));

        StatementItem item = new StatementItem();
        item.setStatementItemId(statementItemId);
        item.setStatementId(statementItemDto.getStatementId());
        item.setTransactionId(statementItemDto.getTransactionId());
        item.setItemDate(statementItemDto.getItemDate());
        item.setDescription(statementItemDto.getDescription());
        item.setAmount(statementItemDto.getAmount());
        item.setItemType(statementItemDto.getItemType());

        StatementItem savedItem = statementItemRepository.save(item);
        return convertToResponseDto(savedItem);
    }

    @Override
    public StatementItemResponseDto getStatementItemById(String statementItemId) {
        StatementItem item = statementItemRepository.findById(statementItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Statement item not found with ID: " + statementItemId));
        return convertToResponseDto(item);
    }

    @Override
    public List<StatementItemResponseDto> getItemsByStatementId(String statementId) {
        return statementItemRepository.findByStatementId(statementId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StatementItemResponseDto> getAllStatementItems() {
        return statementItemRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private StatementItemResponseDto convertToResponseDto(StatementItem item) {
        StatementItemResponseDto dto = new StatementItemResponseDto();
        dto.setStatementItemId(item.getStatementItemId());
        dto.setStatementId(item.getStatementId());
        dto.setTransactionId(item.getTransactionId());
        dto.setItemDate(item.getItemDate());
        dto.setDescription(item.getDescription());
        dto.setAmount(item.getAmount());
        dto.setItemType(item.getItemType());
        return dto;
    }
}
