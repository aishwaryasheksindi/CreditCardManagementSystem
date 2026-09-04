package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardTransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardTransactionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IRewardTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reward-transactions")
public class RewardTransactionController {

    private final IRewardTransactionService rewardTransactionService;

    public RewardTransactionController(IRewardTransactionService rewardTransactionService) {
        this.rewardTransactionService = rewardTransactionService;
    }

    @PostMapping
    public ResponseEntity<RewardTransactionResponseDto> addRewardTransaction(@Valid @RequestBody RewardTransactionRequestDto requestDto) {
        RewardTransactionResponseDto created = rewardTransactionService.addRewardTransaction(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{rewardTransactionId}")
    public ResponseEntity<RewardTransactionResponseDto> getRewardTransactionById(@PathVariable String rewardTransactionId) {
        return ResponseEntity.ok(rewardTransactionService.getRewardTransactionById(rewardTransactionId));
    }

    @GetMapping
    public ResponseEntity<List<RewardTransactionResponseDto>> getAllRewardTransactions(
            @RequestParam(required = false) String rewardId) {
        if (rewardId != null && !rewardId.isBlank()) {
            return ResponseEntity.ok(rewardTransactionService.getRewardTransactionsByRewardId(rewardId));
        }
        return ResponseEntity.ok(rewardTransactionService.getAllRewardTransactions());
    }

    @DeleteMapping("/{rewardTransactionId}")
    public ResponseEntity<Void> deleteRewardTransaction(@PathVariable String rewardTransactionId) {
        rewardTransactionService.deleteRewardTransaction(rewardTransactionId);
        return ResponseEntity.noContent().build();
    }
}
