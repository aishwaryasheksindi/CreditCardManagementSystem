package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.EmiPlan;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.repository.EmiPlanRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.EmiPlanServiceImpl;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmiPlanServiceTest {

    @Mock
    private EmiPlanRepository emiPlanRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IAuditLogService auditLogService;

    @InjectMocks
    private EmiPlanServiceImpl emiPlanService;

    @Test
    void testCreateEmiPlan_ThrowsException_WhenTransactionAmountLessThan3000() {
        String txnId = "TXN100001";
        Transaction transaction = new Transaction();
        transaction.setTransactionId(txnId);
        transaction.setAmount(new BigDecimal("2500.00"));

        when(transactionRepository.findById(txnId)).thenReturn(Optional.of(transaction));

        EmiPlanRequestDto requestDto = new EmiPlanRequestDto();
        requestDto.setTransactionId(txnId);
        requestDto.setPrincipal(new BigDecimal("2500.00"));
        requestDto.setInterestRate(new BigDecimal("12.5"));
        requestDto.setTenureMonths(6);
        requestDto.setEmiAmount(new BigDecimal("432.00"));
        requestDto.setProcessingFee(new BigDecimal("99.00"));
        requestDto.setStartDate(LocalDate.now());
        requestDto.setEndDate(LocalDate.now().plusMonths(6));
        requestDto.setOutstandingAmount(new BigDecimal("2500.00"));
        requestDto.setStatus("ACTIVE");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                emiPlanService.createEmiPlan(requestDto)
        );

        assertTrue(ex.getMessage().contains("at least ₹3000"));
        verify(emiPlanRepository, never()).save(any());
    }

    @Test
    void testCreateEmiPlan_Success_WhenTransactionAmountAtLeast3000() {
        String txnId = "TXN100002";
        Transaction transaction = new Transaction();
        transaction.setTransactionId(txnId);
        transaction.setAmount(new BigDecimal("5000.00"));

        when(transactionRepository.findById(txnId)).thenReturn(Optional.of(transaction));
        when(emiPlanRepository.existsById(any())).thenReturn(false);
        when(emiPlanRepository.save(any(EmiPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmiPlanRequestDto requestDto = new EmiPlanRequestDto();
        requestDto.setTransactionId(txnId);
        requestDto.setPrincipal(new BigDecimal("5000.00"));
        requestDto.setInterestRate(new BigDecimal("14.0"));
        requestDto.setTenureMonths(12);
        requestDto.setEmiAmount(new BigDecimal("450.00"));
        requestDto.setProcessingFee(new BigDecimal("150.00"));
        requestDto.setStartDate(LocalDate.now());
        requestDto.setEndDate(LocalDate.now().plusMonths(12));
        requestDto.setOutstandingAmount(new BigDecimal("5000.00"));
        requestDto.setStatus("ACTIVE");

        EmiPlanResponseDto result = emiPlanService.createEmiPlan(requestDto);

        assertNotNull(result);
        assertNotNull(result.getEmiPlanId());
        assertTrue(result.getEmiPlanId().startsWith("EMI"));
        assertEquals(txnId, result.getTransactionId());
        assertEquals(12, result.getTenureMonths());
        assertEquals("ACTIVE", result.getStatus());
        verify(emiPlanRepository, times(1)).save(any(EmiPlan.class));
    }

    @Test
    void testGetEmiPlanById_Success() {
        String emiPlanId = "EMI100001";
        EmiPlan emiPlan = new EmiPlan();
        emiPlan.setEmiPlanId(emiPlanId);
        emiPlan.setTransactionId("TXN100003");
        emiPlan.setPrincipal(new BigDecimal("10000.00"));
        emiPlan.setInterestRate(new BigDecimal("13.5"));
        emiPlan.setTenureMonths(9);
        emiPlan.setEmiAmount(new BigDecimal("1175.00"));
        emiPlan.setProcessingFee(new BigDecimal("200.00"));
        emiPlan.setStartDate(LocalDate.now());
        emiPlan.setEndDate(LocalDate.now().plusMonths(9));
        emiPlan.setOutstandingAmount(new BigDecimal("10000.00"));
        emiPlan.setStatus("ACTIVE");

        when(emiPlanRepository.findById(emiPlanId)).thenReturn(Optional.of(emiPlan));

        EmiPlanResponseDto result = emiPlanService.getEmiPlanById(emiPlanId);

        assertNotNull(result);
        assertEquals(emiPlanId, result.getEmiPlanId());
        assertEquals(9, result.getTenureMonths());
    }

    @Test
    void testRecordLatePayment_Success_Min100Fee() {
        String emiPlanId = "EMI100001";
        LocalDate overdueDate = LocalDate.now().minusDays(5);
        EmiPlan emiPlan = new EmiPlan();
        emiPlan.setEmiPlanId(emiPlanId);
        emiPlan.setTransactionId("TXN100003");
        emiPlan.setEmiAmount(new BigDecimal("2000.00")); // 2% of 2000 is 40 -> minimum 100 applied
        emiPlan.setNextDueDate(overdueDate);
        emiPlan.setLateFeeAmount(BigDecimal.ZERO);
        emiPlan.setMissedInstallments(0);

        when(emiPlanRepository.findById(emiPlanId)).thenReturn(Optional.of(emiPlan));
        when(emiPlanRepository.save(any(EmiPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmiPlanResponseDto response = emiPlanService.recordLatePayment(emiPlanId);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getLateFeeAmount());
        assertEquals(1, response.getMissedInstallments());
        assertEquals(overdueDate.plusMonths(1), response.getNextDueDate());
        verify(emiPlanRepository).save(emiPlan);
        verify(auditLogService).logAction(anyString(), eq(com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction.UPDATE), eq("EmiPlan"), eq(emiPlanId), contains("Late fee"));
    }

    @Test
    void testRecordLatePayment_Success_AboveMin100Fee() {
        String emiPlanId = "EMI100002";
        LocalDate overdueDate = LocalDate.now().minusDays(2);
        EmiPlan emiPlan = new EmiPlan();
        emiPlan.setEmiPlanId(emiPlanId);
        emiPlan.setTransactionId("TXN100004");
        emiPlan.setEmiAmount(new BigDecimal("10000.00")); // 2% of 10000 is 200 (> 100)
        emiPlan.setNextDueDate(overdueDate);
        emiPlan.setLateFeeAmount(new BigDecimal("50.00")); // prior late fee
        emiPlan.setMissedInstallments(1);

        when(emiPlanRepository.findById(emiPlanId)).thenReturn(Optional.of(emiPlan));
        when(emiPlanRepository.save(any(EmiPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmiPlanResponseDto response = emiPlanService.recordLatePayment(emiPlanId);

        assertNotNull(response);
        assertEquals(new BigDecimal("250.00"), response.getLateFeeAmount()); // 50 + 200
        assertEquals(2, response.getMissedInstallments());
        assertEquals(overdueDate.plusMonths(1), response.getNextDueDate());
    }

    @Test
    void testRecordLatePayment_NotOverdue_ThrowsIllegalArgumentException() {
        String emiPlanId = "EMI100003";
        LocalDate futureDueDate = LocalDate.now().plusDays(10);
        EmiPlan emiPlan = new EmiPlan();
        emiPlan.setEmiPlanId(emiPlanId);
        emiPlan.setNextDueDate(futureDueDate);

        when(emiPlanRepository.findById(emiPlanId)).thenReturn(Optional.of(emiPlan));

        assertThrows(IllegalArgumentException.class, () -> emiPlanService.recordLatePayment(emiPlanId));
        verify(emiPlanRepository, never()).save(any());
    }
}
