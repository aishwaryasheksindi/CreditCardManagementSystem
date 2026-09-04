package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.SpendingInsightResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.SpendingInsight;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.SpendingInsightRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpendingInsightServiceImpl implements ISpendingInsightService {

    private final SpendingInsightRepository spendingInsightRepository;
    private final CustomerRepository customerRepository;

    public SpendingInsightServiceImpl(SpendingInsightRepository spendingInsightRepository,
                                      CustomerRepository customerRepository) {
        this.spendingInsightRepository = spendingInsightRepository;
        this.customerRepository = customerRepository;
    }

    private String generateUniqueInsightId() {
        String id;
        do {
            id = IdGenerationUtil.generateInsightId();
        } while (spendingInsightRepository.existsById(id));
        return id;
    }

    private void validateCustomerExists(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public SpendingInsightResponseDto createInsight(SpendingInsightRequestDto requestDto) {
        validateCustomerExists(requestDto.getCustomerId());

        SpendingInsight insight = new SpendingInsight();
        insight.setInsightId(generateUniqueInsightId());
        insight.setCustomerId(requestDto.getCustomerId());
        insight.setObservation(requestDto.getObservation());
        insight.setAmount(requestDto.getAmount());
        insight.setPeriodStart(requestDto.getPeriodStart());
        insight.setPeriodEnd(requestDto.getPeriodEnd());

        SpendingInsight saved = spendingInsightRepository.save(insight);
        return convertToResponseDto(saved);
    }

    @Override
    public SpendingInsightResponseDto getInsightById(String insightId) {
        SpendingInsight insight = spendingInsightRepository.findById(insightId)
                .orElseThrow(() -> new ResourceNotFoundException("Spending insight not found with ID: " + insightId));
        return convertToResponseDto(insight);
    }

    @Override
    public List<SpendingInsightResponseDto> getAllInsights() {
        return spendingInsightRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpendingInsightResponseDto> getInsightsByCustomerId(String customerId) {
        return spendingInsightRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public SpendingInsightResponseDto updateInsight(String insightId, SpendingInsightRequestDto requestDto) {
        SpendingInsight insight = spendingInsightRepository.findById(insightId)
                .orElseThrow(() -> new ResourceNotFoundException("Spending insight not found with ID: " + insightId));

        if (!insight.getCustomerId().equals(requestDto.getCustomerId())) {
            validateCustomerExists(requestDto.getCustomerId());
            insight.setCustomerId(requestDto.getCustomerId());
        }

        insight.setObservation(requestDto.getObservation());
        insight.setAmount(requestDto.getAmount());
        insight.setPeriodStart(requestDto.getPeriodStart());
        insight.setPeriodEnd(requestDto.getPeriodEnd());

        SpendingInsight updated = spendingInsightRepository.save(insight);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteInsight(String insightId) {
        SpendingInsight insight = spendingInsightRepository.findById(insightId)
                .orElseThrow(() -> new ResourceNotFoundException("Spending insight not found with ID: " + insightId));
        spendingInsightRepository.delete(insight);
    }

    private SpendingInsightResponseDto convertToResponseDto(SpendingInsight insight) {
        return new SpendingInsightResponseDto(
                insight.getInsightId(),
                insight.getCustomerId(),
                insight.getObservation(),
                insight.getAmount(),
                insight.getPeriodStart(),
                insight.getPeriodEnd()
        );
    }
}
