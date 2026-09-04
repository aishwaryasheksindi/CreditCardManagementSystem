package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardTransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardTransactionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.RewardTransaction;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RewardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RewardTransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardTransactionServiceImpl implements IRewardTransactionService {

    private final RewardTransactionRepository rewardTransactionRepository;
    private final RewardRepository rewardRepository;

    public RewardTransactionServiceImpl(RewardTransactionRepository rewardTransactionRepository,
                                       RewardRepository rewardRepository) {
        this.rewardTransactionRepository = rewardTransactionRepository;
        this.rewardRepository = rewardRepository;
    }

    private String generateUniqueRewardTransactionId() {
        String txId;
        do {
            txId = IdGenerationUtil.generateRewardTransactionId();
        } while (rewardTransactionRepository.existsById(txId));
        return txId;
    }

    private void validateRewardExists(String rewardId) {
        if (!rewardRepository.existsById(rewardId)) {
            throw new ResourceNotFoundException("Reward not found with ID: " + rewardId);
        }
    }

    @Override
    public RewardTransactionResponseDto addRewardTransaction(RewardTransactionRequestDto requestDto) {
        validateRewardExists(requestDto.getRewardId());

        RewardTransaction transaction = new RewardTransaction();
        transaction.setRewardTransactionId(generateUniqueRewardTransactionId());
        transaction.setRewardId(requestDto.getRewardId());
        transaction.setPoints(requestDto.getPoints());
        transaction.setTransactionType(requestDto.getTransactionType());
        transaction.setDescription(requestDto.getDescription());
        transaction.setTransactionDate(
                requestDto.getTransactionDate() != null ? requestDto.getTransactionDate() : LocalDateTime.now()
        );

        RewardTransaction saved = rewardTransactionRepository.save(transaction);
        return convertToResponseDto(saved);
    }

    @Override
    public RewardTransactionResponseDto getRewardTransactionById(String rewardTransactionId) {
        RewardTransaction transaction = rewardTransactionRepository.findById(rewardTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward transaction not found with ID: " + rewardTransactionId));
        return convertToResponseDto(transaction);
    }

    @Override
    public List<RewardTransactionResponseDto> getAllRewardTransactions() {
        return rewardTransactionRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardTransactionResponseDto> getRewardTransactionsByRewardId(String rewardId) {
        return rewardTransactionRepository.findByRewardId(rewardId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRewardTransaction(String rewardTransactionId) {
        RewardTransaction transaction = rewardTransactionRepository.findById(rewardTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward transaction not found with ID: " + rewardTransactionId));
        rewardTransactionRepository.delete(transaction);
    }

    private RewardTransactionResponseDto convertToResponseDto(RewardTransaction transaction) {
        return new RewardTransactionResponseDto(
                transaction.getRewardTransactionId(),
                transaction.getRewardId(),
                transaction.getPoints(),
                transaction.getTransactionType(),
                transaction.getDescription(),
                transaction.getTransactionDate()
        );
    }
}
