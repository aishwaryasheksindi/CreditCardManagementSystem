package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardTransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardTransactionResponseDto;

import java.util.List;

public interface IRewardTransactionService {

    RewardTransactionResponseDto addRewardTransaction(RewardTransactionRequestDto requestDto);

    RewardTransactionResponseDto getRewardTransactionById(String rewardTransactionId);

    List<RewardTransactionResponseDto> getAllRewardTransactions();

    List<RewardTransactionResponseDto> getRewardTransactionsByRewardId(String rewardId);

    void deleteRewardTransaction(String rewardTransactionId);
}
