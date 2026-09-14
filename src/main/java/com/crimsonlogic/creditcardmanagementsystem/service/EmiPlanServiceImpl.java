package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.EmiPlan;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.EmiPlanRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmiPlanServiceImpl implements IEmiPlanService {

    private static final BigDecimal MIN_EMI_TRANSACTION_AMOUNT = new BigDecimal("3000");

    private final EmiPlanRepository emiPlanRepository;
    private final TransactionRepository transactionRepository;
    private final IAuditLogService auditLogService;
    private final CurrentUserContext currentUserContext;

    public EmiPlanServiceImpl(EmiPlanRepository emiPlanRepository,
                              TransactionRepository transactionRepository,
                              IAuditLogService auditLogService,
                              CurrentUserContext currentUserContext) {
        this.emiPlanRepository = emiPlanRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
        this.currentUserContext = currentUserContext;
    }

    private String generateUniqueEmiPlanId() {
        String emiPlanId;
        do {
            emiPlanId = IdGenerationUtil.generateEmiPlanId();
        } while (emiPlanRepository.existsById(emiPlanId));
        return emiPlanId;
    }

    private void assertEmiPlanBranchAccess(EmiPlan emiPlan) {
        if (currentUserContext == null || emiPlan == null || emiPlan.getTransactionId() == null) {
            return;
        }
        Transaction transaction = transactionRepository.findById(emiPlan.getTransactionId()).orElse(null);
        if (transaction != null && transaction.getCard() != null && transaction.getCard().getCustomer() != null) {
            currentUserContext.assertCustomerBranchAccess(transaction.getCard().getCustomer());
        } else if (currentUserContext.isBankOfficer()) {
            throw new AccessDeniedException("Bank officer is not authorized to access this resource");
        }
    }

    private Transaction validateAndGetTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        if (currentUserContext != null) {
            if (transaction.getCard() != null && transaction.getCard().getCustomer() != null) {
                currentUserContext.assertCustomerBranchAccess(transaction.getCard().getCustomer());
            } else if (currentUserContext.isBankOfficer()) {
                throw new AccessDeniedException("Bank officer is not authorized to access this resource");
            }
        }

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(MIN_EMI_TRANSACTION_AMOUNT) < 0) {
            throw new IllegalArgumentException("Transaction amount must be at least ₹3000 to be eligible for EMI conversion");
        }
        return transaction;
    }

    @Override
    public EmiPlanResponseDto createEmiPlan(EmiPlanRequestDto requestDto) {
        Transaction transaction = validateAndGetTransaction(requestDto.getTransactionId());

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
        emiPlan.setNextDueDate(requestDto.getNextDueDate() != null ? requestDto.getNextDueDate() : requestDto.getStartDate().plusMonths(1));
        emiPlan.setLateFeeAmount(BigDecimal.ZERO);
        emiPlan.setMissedInstallments(0);

        EmiPlan savedEmiPlan = emiPlanRepository.save(emiPlan);

        String customerId = (transaction.getCard() != null && transaction.getCard().getCustomer() != null)
                ? transaction.getCard().getCustomer().getCustomerId()
                : "SYSTEM";

        auditLogService.logAction(
                customerId,
                AuditAction.CREATE,
                "EmiPlan",
                savedEmiPlan.getEmiPlanId(),
                "EMI plan created for transaction " + requestDto.getTransactionId() + " with tenure " + requestDto.getTenureMonths() + " months"
        );

        return convertToResponseDto(savedEmiPlan);
    }

    @Override
    public EmiPlanResponseDto getEmiPlanById(String emiPlanId) {
        EmiPlan emiPlan = emiPlanRepository.findById(emiPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI Plan not found with ID: " + emiPlanId));
        assertEmiPlanBranchAccess(emiPlan);
        return convertToResponseDto(emiPlan);
    }

    @Override
    public List<EmiPlanResponseDto> getAllEmiPlans() {
        if (currentUserContext != null && currentUserContext.isBankOfficer()) {
            String officerBranch = currentUserContext.getCurrentOfficerBranchCode();
            if (officerBranch == null) {
                return Collections.emptyList();
            }
            return emiPlanRepository.findAll()
                    .stream()
                    .filter(plan -> {
                        Transaction tx = transactionRepository.findById(plan.getTransactionId()).orElse(null);
                        return tx != null && tx.getCard() != null && tx.getCard().getCustomer() != null
                                && officerBranch.equalsIgnoreCase(tx.getCard().getCustomer().getBranchCode());
                    })
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
        }
        if (currentUserContext != null && currentUserContext.hasRole("ROLE_CUSTOMER")) {
            String customerId = currentUserContext.getCurrentCustomerId();
            if (customerId == null) {
                return Collections.emptyList();
            }
            return emiPlanRepository.findAll()
                    .stream()
                    .filter(plan -> {
                        Transaction tx = transactionRepository.findById(plan.getTransactionId()).orElse(null);
                        return tx != null && tx.getCard() != null && tx.getCard().getCustomer() != null
                                && customerId.equals(tx.getCard().getCustomer().getCustomerId());
                    })
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
        }
        return emiPlanRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmiPlanResponseDto> getEmiPlansByTransactionId(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
        if (currentUserContext != null) {
            if (transaction.getCard() != null && transaction.getCard().getCustomer() != null) {
                currentUserContext.assertCustomerBranchAccess(transaction.getCard().getCustomer());
            } else if (currentUserContext.isBankOfficer()) {
                throw new AccessDeniedException("Bank officer is not authorized to access this resource");
            }
        }
        return emiPlanRepository.findByTransactionId(transactionId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmiPlanResponseDto updateEmiPlan(String emiPlanId, EmiPlanRequestDto requestDto) {
        EmiPlan emiPlan = emiPlanRepository.findById(emiPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI Plan not found with ID: " + emiPlanId));
        assertEmiPlanBranchAccess(emiPlan);

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
        assertEmiPlanBranchAccess(emiPlan);
        emiPlanRepository.delete(emiPlan);
    }

    @Override
    public EmiPlanResponseDto recordLatePayment(String emiPlanId) {
        EmiPlan emiPlan = emiPlanRepository.findById(emiPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI Plan not found with ID: " + emiPlanId));
        assertEmiPlanBranchAccess(emiPlan);

        if (emiPlan.getNextDueDate() == null || !LocalDate.now().isAfter(emiPlan.getNextDueDate())) {
            throw new IllegalArgumentException("EMI payment is not overdue");
        }

        // Rule #4: 2% of EMI amount, minimum ₹100
        BigDecimal lateFee = emiPlan.getEmiAmount().multiply(new BigDecimal("0.02")).setScale(2, java.math.RoundingMode.HALF_UP);
        if (lateFee.compareTo(new BigDecimal("100.00")) < 0) {
            lateFee = new BigDecimal("100.00");
        }

        BigDecimal currentLateFee = emiPlan.getLateFeeAmount() != null ? emiPlan.getLateFeeAmount() : BigDecimal.ZERO;
        emiPlan.setLateFeeAmount(currentLateFee.add(lateFee));

        BigDecimal currentOutstanding = emiPlan.getOutstandingAmount() != null
                ? emiPlan.getOutstandingAmount() : BigDecimal.ZERO;
        emiPlan.setOutstandingAmount(currentOutstanding.add(lateFee));

        int missed = (emiPlan.getMissedInstallments() != null ? emiPlan.getMissedInstallments() : 0) + 1;
        emiPlan.setMissedInstallments(missed);

        // Advance next due date by 1 month
        emiPlan.setNextDueDate(emiPlan.getNextDueDate().plusMonths(1));

        EmiPlan savedEmiPlan = emiPlanRepository.save(emiPlan);

        String customerId = "SYSTEM";
        Transaction transaction = transactionRepository.findById(emiPlan.getTransactionId()).orElse(null);
        if (transaction != null && transaction.getCard() != null && transaction.getCard().getCustomer() != null) {
            customerId = transaction.getCard().getCustomer().getCustomerId();
        }

        auditLogService.logAction(
                customerId,
                AuditAction.UPDATE,
                "EmiPlan",
                emiPlanId,
                "Late fee of ₹" + lateFee + " applied to EMI plan " + emiPlanId + ". Missed installments: " + missed
        );

        return convertToResponseDto(savedEmiPlan);
    }

    private EmiPlanResponseDto convertToResponseDto(EmiPlan emiPlan) {
        EmiPlanResponseDto dto = new EmiPlanResponseDto(
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
        dto.setNextDueDate(emiPlan.getNextDueDate());
        dto.setLateFeeAmount(emiPlan.getLateFeeAmount());
        dto.setMissedInstallments(emiPlan.getMissedInstallments());
        return dto;
    }
}
