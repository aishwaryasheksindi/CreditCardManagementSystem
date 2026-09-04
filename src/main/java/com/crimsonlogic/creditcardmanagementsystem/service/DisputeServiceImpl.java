package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeUpdateRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Dispute;
import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.DisputeRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisputeServiceImpl implements IDisputeService {

    private final DisputeRepository disputeRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public DisputeServiceImpl(DisputeRepository disputeRepository,
                              CustomerRepository customerRepository,
                              TransactionRepository transactionRepository) {
        this.disputeRepository = disputeRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    private String generateUniqueDisputeId() {
        String disputeId;
        do {
            disputeId = IdGenerationUtil.generateDisputeId();
        } while (disputeRepository.existsById(disputeId));
        return disputeId;
    }

    private void validateCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    private void validateTransaction(String transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + transactionId);
        }
    }

    @Override
    public DisputeResponseDto raiseDispute(DisputeRequestDto requestDto) {
        validateCustomer(requestDto.getCustomerId());
        validateTransaction(requestDto.getTransactionId());

        Dispute dispute = new Dispute();
        dispute.setDisputeId(generateUniqueDisputeId());
        dispute.setCustomerId(requestDto.getCustomerId());
        dispute.setTransactionId(requestDto.getTransactionId());
        dispute.setDisputeType(requestDto.getDisputeType());
        dispute.setStatus(DisputeStatus.RAISED);
        dispute.setDescription(requestDto.getDescription());
        dispute.setEvidenceReference(requestDto.getEvidenceReference());
        dispute.setRaisedAt(LocalDateTime.now());

        Dispute saved = disputeRepository.save(dispute);
        return convertToResponseDto(saved);
    }

    @Override
    public DisputeResponseDto getDisputeById(String disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found with ID: " + disputeId));
        return convertToResponseDto(dispute);
    }

    @Override
    public List<DisputeResponseDto> getAllDisputes() {
        return disputeRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DisputeResponseDto> getDisputesByCustomerId(String customerId) {
        return disputeRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DisputeResponseDto> getDisputesByTransactionId(String transactionId) {
        return disputeRepository.findByTransactionId(transactionId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public DisputeResponseDto updateDispute(String disputeId, DisputeUpdateRequestDto updateDto) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found with ID: " + disputeId));

        if (updateDto.getStatus() != null) {
            dispute.setStatus(updateDto.getStatus());
            if ((updateDto.getStatus() == DisputeStatus.RESOLVED || updateDto.getStatus() == DisputeStatus.CLOSED)
                    && dispute.getResolvedAt() == null) {
                dispute.setResolvedAt(LocalDateTime.now());
            }
        }

        if (updateDto.getInvestigatorStaffId() != null) {
            dispute.setInvestigatorStaffId(updateDto.getInvestigatorStaffId());
        }

        if (updateDto.getResolution() != null) {
            dispute.setResolution(updateDto.getResolution());
        }

        Dispute saved = disputeRepository.save(dispute);
        return convertToResponseDto(saved);
    }

    @Override
    public void deleteDispute(String disputeId) {
        if (!disputeRepository.existsById(disputeId)) {
            throw new ResourceNotFoundException("Dispute not found with ID: " + disputeId);
        }
        disputeRepository.deleteById(disputeId);
    }

    private DisputeResponseDto convertToResponseDto(Dispute dispute) {
        return new DisputeResponseDto(
                dispute.getDisputeId(),
                dispute.getTransactionId(),
                dispute.getCustomerId(),
                dispute.getDisputeType(),
                dispute.getStatus(),
                dispute.getDescription(),
                dispute.getEvidenceReference(),
                dispute.getInvestigatorStaffId(),
                dispute.getRaisedAt(),
                dispute.getResolvedAt(),
                dispute.getResolution()
        );
    }
}
