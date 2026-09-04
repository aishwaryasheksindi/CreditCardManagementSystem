package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CreditRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CreditRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.CreditRecommendation;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CreditRecommendationRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditRecommendationServiceImpl implements ICreditRecommendationService {

    private final CreditRecommendationRepository creditRecommendationRepository;
    private final CustomerRepository customerRepository;

    public CreditRecommendationServiceImpl(CreditRecommendationRepository creditRecommendationRepository,
                                            CustomerRepository customerRepository) {
        this.creditRecommendationRepository = creditRecommendationRepository;
        this.customerRepository = customerRepository;
    }

    private String generateUniqueRecommendationId() {
        String id;
        do {
            id = IdGenerationUtil.generateCreditRecommendationId();
        } while (creditRecommendationRepository.existsById(id));
        return id;
    }

    private void validateCustomerExists(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public CreditRecommendationResponseDto createRecommendation(CreditRecommendationRequestDto requestDto) {
        validateCustomerExists(requestDto.getCustomerId());

        CreditRecommendation rec = new CreditRecommendation();
        rec.setRecommendationId(generateUniqueRecommendationId());
        rec.setCustomerId(requestDto.getCustomerId());
        rec.setCurrentLimit(requestDto.getCurrentLimit());
        rec.setRecommendedMin(requestDto.getRecommendedMin());
        rec.setRecommendedMax(requestDto.getRecommendedMax());
        rec.setFactors(requestDto.getFactors());

        CreditRecommendation saved = creditRecommendationRepository.save(rec);
        return convertToResponseDto(saved);
    }

    @Override
    public CreditRecommendationResponseDto getRecommendationById(String recommendationId) {
        CreditRecommendation rec = creditRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit recommendation not found with ID: " + recommendationId));
        return convertToResponseDto(rec);
    }

    @Override
    public List<CreditRecommendationResponseDto> getAllRecommendations() {
        return creditRecommendationRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CreditRecommendationResponseDto> getRecommendationsByCustomerId(String customerId) {
        return creditRecommendationRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CreditRecommendationResponseDto updateRecommendation(String recommendationId, CreditRecommendationRequestDto requestDto) {
        CreditRecommendation rec = creditRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit recommendation not found with ID: " + recommendationId));

        if (!rec.getCustomerId().equals(requestDto.getCustomerId())) {
            validateCustomerExists(requestDto.getCustomerId());
            rec.setCustomerId(requestDto.getCustomerId());
        }

        rec.setCurrentLimit(requestDto.getCurrentLimit());
        rec.setRecommendedMin(requestDto.getRecommendedMin());
        rec.setRecommendedMax(requestDto.getRecommendedMax());
        rec.setFactors(requestDto.getFactors());

        CreditRecommendation updated = creditRecommendationRepository.save(rec);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteRecommendation(String recommendationId) {
        CreditRecommendation rec = creditRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit recommendation not found with ID: " + recommendationId));
        creditRecommendationRepository.delete(rec);
    }

    private CreditRecommendationResponseDto convertToResponseDto(CreditRecommendation rec) {
        return new CreditRecommendationResponseDto(
                rec.getRecommendationId(),
                rec.getCustomerId(),
                rec.getCurrentLimit(),
                rec.getRecommendedMin(),
                rec.getRecommendedMax(),
                rec.getFactors()
        );
    }
}
