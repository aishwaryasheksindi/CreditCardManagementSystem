package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Reward;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RewardRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.RewardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Test
    void testCreateReward_Success() {
        String customerId = "CUST1001";
        RewardRequestDto requestDto = new RewardRequestDto();
        requestDto.setCustomerId(customerId);
        requestDto.setEarnedPoints(500);
        requestDto.setRedeemedPoints(100);
        requestDto.setExpiredPoints(0);
        requestDto.setBonusPoints(50);
        requestDto.setBalancePoints(450);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(rewardRepository.existsByCustomerId(customerId)).thenReturn(false);
        when(rewardRepository.existsById(any())).thenReturn(false);
        when(rewardRepository.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RewardResponseDto result = rewardService.createReward(requestDto);

        assertNotNull(result);
        assertNotNull(result.getRewardId());
        assertTrue(result.getRewardId().startsWith("RWD"));
        assertEquals(customerId, result.getCustomerId());
        assertEquals(450, result.getBalancePoints());
        verify(rewardRepository, times(1)).save(any(Reward.class));
    }

    @Test
    void testCreateReward_ThrowsResourceNotFoundException_WhenCustomerDoesNotExist() {
        String customerId = "CUST9999";
        RewardRequestDto requestDto = new RewardRequestDto();
        requestDto.setCustomerId(customerId);
        requestDto.setEarnedPoints(100);
        requestDto.setRedeemedPoints(0);
        requestDto.setExpiredPoints(0);
        requestDto.setBonusPoints(0);
        requestDto.setBalancePoints(100);

        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                rewardService.createReward(requestDto)
        );

        verify(rewardRepository, never()).save(any());
    }

    @Test
    void testCreateReward_ThrowsDuplicateResourceException_WhenRewardAlreadyExistsForCustomer() {
        String customerId = "CUST1001";
        RewardRequestDto requestDto = new RewardRequestDto();
        requestDto.setCustomerId(customerId);
        requestDto.setEarnedPoints(100);
        requestDto.setRedeemedPoints(0);
        requestDto.setExpiredPoints(0);
        requestDto.setBonusPoints(0);
        requestDto.setBalancePoints(100);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(rewardRepository.existsByCustomerId(customerId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                rewardService.createReward(requestDto)
        );

        verify(rewardRepository, never()).save(any());
    }

    @Test
    void testGetRewardByCustomerId_Success() {
        String customerId = "CUST1001";
        Reward reward = new Reward("RWD100001", customerId, 1000, 200, 50, 100, 850);

        when(rewardRepository.findByCustomerId(customerId)).thenReturn(Optional.of(reward));

        RewardResponseDto result = rewardService.getRewardByCustomerId(customerId);

        assertNotNull(result);
        assertEquals("RWD100001", result.getRewardId());
        assertEquals(850, result.getBalancePoints());
    }
}
