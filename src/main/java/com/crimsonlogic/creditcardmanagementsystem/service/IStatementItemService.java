package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementItemDto;

import java.util.List;

public interface IStatementItemService {

    StatementItemDto addStatementItem(StatementItemDto statementItemDto);

    StatementItemDto getStatementItemById(String statementItemId);

    List<StatementItemDto> getItemsByStatementId(String statementId);

    List<StatementItemDto> getAllStatementItems();
}
