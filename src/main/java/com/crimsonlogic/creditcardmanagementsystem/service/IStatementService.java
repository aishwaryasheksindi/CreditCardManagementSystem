package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementDto;

import java.util.List;

public interface IStatementService {

    StatementDto addStatement(StatementDto statementDto);

    StatementDto getStatementById(String statementId);

    List<StatementDto> getStatementsByCardId(String cardId);

    List<StatementDto> getAllStatements();
}
