package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Reward;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RewardRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements IRewardService {

    private final RewardRepository rewardRepository;
    private final CustomerRepository customerRepository;

    public RewardServiceImpl(RewardRepository rewardRepository,
                             CustomerRepository customerRepository) {
        this.rewardRepository = rewardRepository;
        this.customerRepository = customerRepository;
    }

    private String generateUniqueRewardId() {
        String rewardId;
        do {
            rewardId = IdGenerationUtil.generateRewardId();
        } while (rewardRepository.existsById(rewardId));
        return rewardId;
    }

    private void validateCustomerExists(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public RewardResponseDto createReward(RewardRequestDto requestDto) {
        validateCustomerExists(requestDto.getCustomerId());

        if (rewardRepository.existsByCustomerId(requestDto.getCustomerId())) {
            throw new DuplicateResourceException("Reward account already exists for customer ID: " + requestDto.getCustomerId());
        }

        Reward reward = new Reward();
        reward.setRewardId(generateUniqueRewardId());
        reward.setCustomerId(requestDto.getCustomerId());
        reward.setEarnedPoints(requestDto.getEarnedPoints());
        reward.setRedeemedPoints(requestDto.getRedeemedPoints());
        reward.setExpiredPoints(requestDto.getExpiredPoints());
        reward.setBonusPoints(requestDto.getBonusPoints());
        reward.setBalancePoints(requestDto.getBalancePoints());

        Reward saved = rewardRepository.save(reward);
        return convertToResponseDto(saved);
    }

    @Override
    public RewardResponseDto getRewardById(String rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));
        return convertToResponseDto(reward);
    }

    @Override
    public RewardResponseDto getRewardByCustomerId(String customerId) {
        Reward reward = rewardRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found for customer ID: " + customerId));
        return convertToResponseDto(reward);
    }

    @Override
    public List<RewardResponseDto> getAllRewards() {
        return rewardRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public RewardResponseDto updateReward(String rewardId, RewardRequestDto requestDto) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));

        if (!reward.getCustomerId().equals(requestDto.getCustomerId())) {
            validateCustomerExists(requestDto.getCustomerId());
            reward.setCustomerId(requestDto.getCustomerId());
        }

        reward.setEarnedPoints(requestDto.getEarnedPoints());
        reward.setRedeemedPoints(requestDto.getRedeemedPoints());
        reward.setExpiredPoints(requestDto.getExpiredPoints());
        reward.setBonusPoints(requestDto.getBonusPoints());
        reward.setBalancePoints(requestDto.getBalancePoints());

        Reward updated = rewardRepository.save(reward);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteReward(String rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));
        rewardRepository.delete(reward);
    }

    private RewardResponseDto convertToResponseDto(Reward reward) {
        return new RewardResponseDto(
                reward.getRewardId(),
                reward.getCustomerId(),
                reward.getEarnedPoints(),
                reward.getRedeemedPoints(),
                reward.getExpiredPoints(),
                reward.getBonusPoints(),
                reward.getBalancePoints()
        );
    }
}
