package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RewardRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.RewardRecommendation;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RewardRecommendationRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardRecommendationServiceImpl implements IRewardRecommendationService {

    private final RewardRecommendationRepository rewardRecommendationRepository;
    private final CustomerRepository customerRepository;

    public RewardRecommendationServiceImpl(RewardRecommendationRepository rewardRecommendationRepository,
                                           CustomerRepository customerRepository) {
        this.rewardRecommendationRepository = rewardRecommendationRepository;
        this.customerRepository = customerRepository;
    }

    private String generateUniqueRecommendationId() {
        String id;
        do {
            id = IdGenerationUtil.generateRewardRecommendationId();
        } while (rewardRecommendationRepository.existsById(id));
        return id;
    }

    private void validateCustomerExists(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public RewardRecommendationResponseDto createRecommendation(RewardRecommendationRequestDto requestDto) {
        validateCustomerExists(requestDto.getCustomerId());

        RewardRecommendation rec = new RewardRecommendation();
        rec.setRecommendationId(generateUniqueRecommendationId());
        rec.setCustomerId(requestDto.getCustomerId());
        rec.setOfferName(requestDto.getOfferName());
        rec.setReason(requestDto.getReason());

        RewardRecommendation saved = rewardRecommendationRepository.save(rec);
        return convertToResponseDto(saved);
    }

    @Override
    public RewardRecommendationResponseDto getRecommendationById(String recommendationId) {
        RewardRecommendation rec = rewardRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward recommendation not found with ID: " + recommendationId));
        return convertToResponseDto(rec);
    }

    @Override
    public List<RewardRecommendationResponseDto> getAllRecommendations() {
        return rewardRecommendationRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardRecommendationResponseDto> getRecommendationsByCustomerId(String customerId) {
        return rewardRecommendationRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public RewardRecommendationResponseDto updateRecommendation(String recommendationId, RewardRecommendationRequestDto requestDto) {
        RewardRecommendation rec = rewardRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward recommendation not found with ID: " + recommendationId));

        if (!rec.getCustomerId().equals(requestDto.getCustomerId())) {
            validateCustomerExists(requestDto.getCustomerId());
            rec.setCustomerId(requestDto.getCustomerId());
        }

        rec.setOfferName(requestDto.getOfferName());
        rec.setReason(requestDto.getReason());

        RewardRecommendation updated = rewardRecommendationRepository.save(rec);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteRecommendation(String recommendationId) {
        RewardRecommendation rec = rewardRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward recommendation not found with ID: " + recommendationId));
        rewardRecommendationRepository.delete(rec);
    }

    private RewardRecommendationResponseDto convertToResponseDto(RewardRecommendation rec) {
        return new RewardRecommendationResponseDto(
                rec.getRecommendationId(),
                rec.getCustomerId(),
                rec.getOfferName(),
                rec.getReason()
        );
    }
}
