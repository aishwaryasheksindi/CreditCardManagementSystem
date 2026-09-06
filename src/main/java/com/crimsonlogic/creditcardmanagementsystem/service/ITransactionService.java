package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionResponseDto;

import java.util.List;

public interface ITransactionService {

    TransactionResponseDto addTransaction(TransactionRequestDto transactionDto);

    TransactionResponseDto getTransactionById(String transactionId);

    List<TransactionResponseDto> getTransactionsByCardId(String cardId);
}