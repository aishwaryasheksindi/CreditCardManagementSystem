package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IRewardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final IRewardService rewardService;

    public RewardController(IRewardService rewardService) {
        this.rewardService = rewardService;
    }

    @PostMapping
    public ResponseEntity<RewardResponseDto> createReward(@Valid @RequestBody RewardRequestDto requestDto) {
        RewardResponseDto created = rewardService.createReward(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{rewardId}")
    public ResponseEntity<RewardResponseDto> getRewardById(@PathVariable String rewardId) {
        return ResponseEntity.ok(rewardService.getRewardById(rewardId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<RewardResponseDto> getRewardByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(rewardService.getRewardByCustomerId(customerId));
    }

    @GetMapping
    public ResponseEntity<List<RewardResponseDto>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @PutMapping("/{rewardId}")
    public ResponseEntity<RewardResponseDto> updateReward(@PathVariable String rewardId,
                                                          @Valid @RequestBody RewardRequestDto requestDto) {
        return ResponseEntity.ok(rewardService.updateReward(rewardId, requestDto));
    }

    @DeleteMapping("/{rewardId}")
    public ResponseEntity<Void> deleteReward(@PathVariable String rewardId) {
        rewardService.deleteReward(rewardId);
        return ResponseEntity.noContent().build();
    }
}
