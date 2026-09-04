package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiRecommendationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiRecommendationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.EmiRecommendation;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.EmiRecommendationRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmiRecommendationServiceImpl implements IEmiRecommendationService {

    private final EmiRecommendationRepository emiRecommendationRepository;
    private final TransactionRepository transactionRepository;

    public EmiRecommendationServiceImpl(EmiRecommendationRepository emiRecommendationRepository,
                                        TransactionRepository transactionRepository) {
        this.emiRecommendationRepository = emiRecommendationRepository;
        this.transactionRepository = transactionRepository;
    }

    private String generateUniqueRecommendationId() {
        String id;
        do {
            id = IdGenerationUtil.generateEmiRecommendationId();
        } while (emiRecommendationRepository.existsById(id));
        return id;
    }

    private void validateTransactionExists(String transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + transactionId);
        }
    }

    @Override
    public EmiRecommendationResponseDto createRecommendation(EmiRecommendationRequestDto requestDto) {
        validateTransactionExists(requestDto.getTransactionId());

        EmiRecommendation rec = new EmiRecommendation();
        rec.setRecommendationId(generateUniqueRecommendationId());
        rec.setTransactionId(requestDto.getTransactionId());
        rec.setTenureMonths(requestDto.getTenureMonths());
        rec.setTotalPayable(requestDto.getTotalPayable());
        rec.setFees(requestDto.getFees());

        EmiRecommendation saved = emiRecommendationRepository.save(rec);
        return convertToResponseDto(saved);
    }

    @Override
    public EmiRecommendationResponseDto getRecommendationById(String recommendationId) {
        EmiRecommendation rec = emiRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI recommendation not found with ID: " + recommendationId));
        return convertToResponseDto(rec);
    }

    @Override
    public List<EmiRecommendationResponseDto> getAllRecommendations() {
        return emiRecommendationRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmiRecommendationResponseDto> getRecommendationsByTransactionId(String transactionId) {
        return emiRecommendationRepository.findByTransactionId(transactionId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmiRecommendationResponseDto updateRecommendation(String recommendationId, EmiRecommendationRequestDto requestDto) {
        EmiRecommendation rec = emiRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI recommendation not found with ID: " + recommendationId));

        if (!rec.getTransactionId().equals(requestDto.getTransactionId())) {
            validateTransactionExists(requestDto.getTransactionId());
            rec.setTransactionId(requestDto.getTransactionId());
        }

        rec.setTenureMonths(requestDto.getTenureMonths());
        rec.setTotalPayable(requestDto.getTotalPayable());
        rec.setFees(requestDto.getFees());

        EmiRecommendation updated = emiRecommendationRepository.save(rec);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteRecommendation(String recommendationId) {
        EmiRecommendation rec = emiRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI recommendation not found with ID: " + recommendationId));
        emiRecommendationRepository.delete(rec);
    }

    private EmiRecommendationResponseDto convertToResponseDto(EmiRecommendation rec) {
        return new EmiRecommendationResponseDto(
                rec.getRecommendationId(),
                rec.getTransactionId(),
                rec.getTenureMonths(),
                rec.getTotalPayable(),
                rec.getFees()
        );
    }
}
