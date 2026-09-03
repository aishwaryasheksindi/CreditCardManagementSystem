package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionResponseDto;

public interface ITransactionService {

    TransactionResponseDto addTransaction(TransactionRequestDto transactionDto);

    TransactionResponseDto getTransactionById(String transactionId);
}