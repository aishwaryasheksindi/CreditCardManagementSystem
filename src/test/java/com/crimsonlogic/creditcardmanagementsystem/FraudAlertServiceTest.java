package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.FraudAlertResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.FraudAlert;
import com.crimsonlogic.creditcardmanagementsystem.repository.FraudAlertRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RiskScoreRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StaffRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.FraudAlertServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudAlertServiceTest {

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RiskScoreRepository riskScoreRepository;

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private FraudAlertServiceImpl fraudAlertService;

    @Test
    void testCreateFraudAlert_Success_OpenStatus() {
        String txnId = "TXN100001";
        when(transactionRepository.existsById(txnId)).thenReturn(true);
        when(fraudAlertRepository.existsById(any())).thenReturn(false);
        when(fraudAlertRepository.save(any(FraudAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        FraudAlertRequestDto requestDto = new FraudAlertRequestDto(
                txnId, null, "OPEN", "Suspicious overseas location", null
        );

        FraudAlertResponseDto result = fraudAlertService.createFraudAlert(requestDto);

        assertNotNull(result);
        assertTrue(result.getFraudAlertId().startsWith("FA"));
        assertEquals("OPEN", result.getStatus());
        assertNotNull(result.getRaisedAt());
        assertNull(result.getClosedAt());
        verify(fraudAlertRepository, times(1)).save(any(FraudAlert.class));
    }

    @Test
    void testUpdateFraudAlert_Success_ClosedStatusSetsClosedAt() {
        String alertId = "FA100001";
        String txnId = "TXN100001";

        FraudAlert existingAlert = new FraudAlert();
        existingAlert.setFraudAlertId(alertId);
        existingAlert.setTransactionId(txnId);
        existingAlert.setStatus("OPEN");
        existingAlert.setReason("Suspicious transaction");
        existingAlert.setRaisedAt(LocalDateTime.now().minusHours(2));
        existingAlert.setClosedAt(null);

        when(fraudAlertRepository.findById(alertId)).thenReturn(Optional.of(existingAlert));
        when(fraudAlertRepository.save(any(FraudAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        FraudAlertRequestDto updateDto = new FraudAlertRequestDto(
                txnId, null, "CLOSED", "Confirmed false alarm by cardholder", "STF1001"
        );
        when(staffRepository.existsById("STF1001")).thenReturn(true);

        FraudAlertResponseDto result = fraudAlertService.updateFraudAlert(alertId, updateDto);

        assertNotNull(result);
        assertEquals("CLOSED", result.getStatus());
        assertNotNull(result.getClosedAt());
        assertEquals("STF1001", result.getInvestigatorStaffId());
    }
}
