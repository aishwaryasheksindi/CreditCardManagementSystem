package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.FraudAlert;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.FraudAlertRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RiskScoreRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StaffRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FraudAlertServiceImpl implements IFraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionRepository transactionRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final StaffRepository staffRepository;

    public FraudAlertServiceImpl(FraudAlertRepository fraudAlertRepository,
                                 TransactionRepository transactionRepository,
                                 RiskScoreRepository riskScoreRepository,
                                 StaffRepository staffRepository) {
        this.fraudAlertRepository = fraudAlertRepository;
        this.transactionRepository = transactionRepository;
        this.riskScoreRepository = riskScoreRepository;
        this.staffRepository = staffRepository;
    }

    private String generateUniqueFraudAlertId() {
        String id;
        do {
            id = IdGenerationUtil.generateFraudAlertId();
        } while (fraudAlertRepository.existsById(id));
        return id;
    }

    private void validateTransactionExists(String transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + transactionId);
        }
    }

    private void validateRiskScoreExists(String riskScoreId) {
        if (riskScoreId != null && !riskScoreId.isBlank() && !riskScoreRepository.existsById(riskScoreId)) {
            throw new ResourceNotFoundException("Risk score not found with ID: " + riskScoreId);
        }
    }

    private void validateStaffExists(String staffId) {
        if (staffId != null && !staffId.isBlank() && !staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException("Staff not found with ID: " + staffId);
        }
    }

    private boolean isClosedStatus(String status) {
        return "CONFIRMED".equalsIgnoreCase(status)
                || "FALSE_POSITIVE".equalsIgnoreCase(status)
                || "CLOSED".equalsIgnoreCase(status);
    }

    @Override
    public FraudAlertResponseDto createFraudAlert(FraudAlertRequestDto requestDto) {
        validateTransactionExists(requestDto.getTransactionId());
        validateRiskScoreExists(requestDto.getRiskScoreId());
        validateStaffExists(requestDto.getInvestigatorStaffId());

        FraudAlert alert = new FraudAlert();
        alert.setFraudAlertId(generateUniqueFraudAlertId());
        alert.setTransactionId(requestDto.getTransactionId());
        alert.setRiskScoreId(requestDto.getRiskScoreId());
        alert.setStatus(requestDto.getStatus());
        alert.setReason(requestDto.getReason());
        alert.setInvestigatorStaffId(requestDto.getInvestigatorStaffId());
        alert.setRaisedAt(LocalDateTime.now());
        if (isClosedStatus(requestDto.getStatus())) {
            alert.setClosedAt(LocalDateTime.now());
        }

        FraudAlert saved = fraudAlertRepository.save(alert);
        return convertToResponseDto(saved);
    }

    @Override
    public FraudAlertResponseDto getFraudAlertById(String fraudAlertId) {
        FraudAlert alert = fraudAlertRepository.findById(fraudAlertId)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud alert not found with ID: " + fraudAlertId));
        return convertToResponseDto(alert);
    }

    @Override
    public List<FraudAlertResponseDto> getAllFraudAlerts() {
        return fraudAlertRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FraudAlertResponseDto> getFraudAlertsByTransactionId(String transactionId) {
        return fraudAlertRepository.findByTransactionId(transactionId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FraudAlertResponseDto> getFraudAlertsByStatus(String status) {
        return fraudAlertRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public FraudAlertResponseDto updateFraudAlert(String fraudAlertId, FraudAlertRequestDto requestDto) {
        FraudAlert alert = fraudAlertRepository.findById(fraudAlertId)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud alert not found with ID: " + fraudAlertId));

        if (!alert.getTransactionId().equals(requestDto.getTransactionId())) {
            validateTransactionExists(requestDto.getTransactionId());
            alert.setTransactionId(requestDto.getTransactionId());
        }

        validateRiskScoreExists(requestDto.getRiskScoreId());
        validateStaffExists(requestDto.getInvestigatorStaffId());

        alert.setRiskScoreId(requestDto.getRiskScoreId());
        alert.setReason(requestDto.getReason());
        alert.setInvestigatorStaffId(requestDto.getInvestigatorStaffId());

        String newStatus = requestDto.getStatus();
        if (isClosedStatus(newStatus) && alert.getClosedAt() == null) {
            alert.setClosedAt(LocalDateTime.now());
        } else if (!isClosedStatus(newStatus)) {
            alert.setClosedAt(null);
        }
        alert.setStatus(newStatus);

        FraudAlert updated = fraudAlertRepository.save(alert);
        return convertToResponseDto(updated);
    }

    @Override
    public void deleteFraudAlert(String fraudAlertId) {
        FraudAlert alert = fraudAlertRepository.findById(fraudAlertId)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud alert not found with ID: " + fraudAlertId));
        fraudAlertRepository.delete(alert);
    }

    private FraudAlertResponseDto convertToResponseDto(FraudAlert alert) {
        return new FraudAlertResponseDto(
                alert.getFraudAlertId(),
                alert.getTransactionId(),
                alert.getRiskScoreId(),
                alert.getStatus(),
                alert.getReason(),
                alert.getInvestigatorStaffId(),
                alert.getRaisedAt(),
                alert.getClosedAt()
        );
    }
}
