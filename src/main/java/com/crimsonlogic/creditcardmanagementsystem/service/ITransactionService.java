package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionDto;

public interface ITransactionService {

    TransactionDto addTransaction(TransactionDto transactionDto);

    TransactionDto getTransactionById(String transactionId);
}