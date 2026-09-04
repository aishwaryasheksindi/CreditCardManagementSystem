package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.EmiPlan;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.EmiPlanRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmiPlanServiceImpl implements IEmiPlanService {

    private static final BigDecimal MIN_EMI_TRANSACTION_AMOUNT = new BigDecimal("3000");

    private final EmiPlanRepository emiPlanRepository;
    private final TransactionRepository transactionRepository;

    public EmiPlanServiceImpl(EmiPlanRepository emiPlanRepository,
                              TransactionRepository transactionRepository) {
        this.emiPlanRepository = emiPlanRepository;
        this.transactionRepository = transactionRepository;
    }

    private String generateUniqueEmiPlanId() {
        String emiPlanId;
        do {
            emiPlanId = IdGenerationUtil.generateEmiPlanId();
        } while (emiPlanRepository.existsById(emiPlanId));
        return emiPlanId;
    }

    private Transaction validateAndGetTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(MIN_EMI_TRANSACTION_AMOUNT) < 0) {
            throw new IllegalArgumentException("Transaction amount must be at least ₹3000 to be eligible for EMI conversion");
        }
        return transaction;
    }

    @Override
    public EmiPlanResponseDto createEmiPlan(EmiPlanRequestDto requestDto) {
        validateAndGetTransaction(requestDto.getTransactionId());

        EmiPlan emiPlan = new EmiPlan();
        emiPlan.setEmiPlanId(generateUniqueEmiPlanId());
        emiPlan.setTransactionId(requestDto.getTransactionId());
        emiPlan.setPrincipal(requestDto.getPrincipal());
        emiPlan.setInterestRate(requestDto.getInterestRate());
        emiPlan.setTenureMonths(requestDto.getTenureMonths());
        emiPlan.setEmiAmount(requestDto.getEmiAmount());
        emiPlan.setProcessingFee(requestDto.getProcessingFee());
        emiPlan.setStartDate(requestDto.getStartDate());
        emiPlan.setEndDate(requestDto.getEndDate());
        emiPlan.setOutstandingAmount(requestDto.getOutstandingAmount());
        emiPlan.setStatus(requestDto.getStatus());

        EmiPlan savedEmiPlan = emiPlanRepository.save(emiPlan);
        return convertToResponseDto(savedEmiPlan);
    }

    @Override
    public EmiPlanResponseDto getEmiPlanById(String emiPlanId) {
        EmiPlan emiPlan = emiPlanRepository.findById(emiPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI Plan not found with ID: " + emiPlanId));
        return convertToResponseDto(emiPlan);
    }

    @Override
    public List<EmiPlanResponseDto> getAllEmiPlans() {
        return emiPlanRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmiPlanResponseDto> getEmiPlansByTransactionId(String transactionId) {
        return emiPlanRepository.findByTransactionId(transactionId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmiPlanResponseDto updateEmiPlan(String emiPlanId, EmiPlanRequestDto requestDto) {
        EmiPlan emiPlan = emiPlanRepository.findById(emiPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI Plan not found with ID: " + emiPlanId));

        validateAndGetTransaction(requestDto.getTransactionId());

        emiPlan.setTransactionId(requestDto.getTransactionId());
        emiPlan.setPrincipal(requestDto.getPrincipal());
        emiPlan.setInterestRate(requestDto.getInterestRate());
        emiPlan.setTenureMonths(requestDto.getTenureMonths());
        emiPlan.setEmiAmount(requestDto.getEmiAmount());
        emiPlan.setProcessingFee(requestDto.getProcessingFee());
        emiPlan.setStartDate(requestDto.getStartDate());
        emiPlan.setEndDate(requestDto.getEndDate());
        emiPlan.setOutstandingAmount(requestDto.getOutstandingAmount());
        emiPlan.setStatus(requestDto.getStatus());

        EmiPlan updatedEmiPlan = emiPlanRepository.save(emiPlan);
        return convertToResponseDto(updatedEmiPlan);
    }

    @Override
    public void deleteEmiPlan(String emiPlanId) {
        EmiPlan emiPlan = emiPlanRepository.findById(emiPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI Plan not found with ID: " + emiPlanId));
        emiPlanRepository.delete(emiPlan);
    }

    private EmiPlanResponseDto convertToResponseDto(EmiPlan emiPlan) {
        return new EmiPlanResponseDto(
                emiPlan.getEmiPlanId(),
                emiPlan.getTransactionId(),
                emiPlan.getPrincipal(),
                emiPlan.getInterestRate(),
                emiPlan.getTenureMonths(),
                emiPlan.getEmiAmount(),
                emiPlan.getProcessingFee(),
                emiPlan.getStartDate(),
                emiPlan.getEndDate(),
                emiPlan.getOutstandingAmount(),
                emiPlan.getStatus()
        );
    }
}
