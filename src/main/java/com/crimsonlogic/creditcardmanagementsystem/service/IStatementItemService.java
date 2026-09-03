package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemResponseDto;

import java.util.List;

public interface IStatementItemService {

    StatementItemResponseDto addStatementItem(StatementItemRequestDto statementItemDto);

    StatementItemResponseDto getStatementItemById(String statementItemId);

    List<StatementItemResponseDto> getItemsByStatementId(String statementId);

    List<StatementItemResponseDto> getAllStatementItems();
}
