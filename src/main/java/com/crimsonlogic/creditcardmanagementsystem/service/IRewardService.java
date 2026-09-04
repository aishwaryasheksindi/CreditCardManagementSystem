package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardResponseDto;

import java.util.List;

public interface IRewardService {

    RewardResponseDto createReward(RewardRequestDto requestDto);

    RewardResponseDto getRewardById(String rewardId);

    RewardResponseDto getRewardByCustomerId(String customerId);

    List<RewardResponseDto> getAllRewards();

    RewardResponseDto updateReward(String rewardId, RewardRequestDto requestDto);

    void deleteReward(String rewardId);
}
