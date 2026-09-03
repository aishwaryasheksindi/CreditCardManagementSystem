package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementResponseDto;

import java.util.List;

public interface IStatementService {

    StatementResponseDto addStatement(StatementRequestDto statementDto);

    StatementResponseDto getStatementById(String statementId);

    List<StatementResponseDto> getStatementsByCardId(String cardId);

    List<StatementResponseDto> getAllStatements();
}
