package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.StatementRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.StatementResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Statement;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StatementRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.StatementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
class StatementServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private StatementServiceImpl statementService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAddStatement_MinimumDueCalculation_FloorApplied() {
        // Closing balance: 25000 + 1000 - 0 - 0 + 0 + 0 = 26000 -> 5% is 1300 (> 200)
        // Closing balance: 1000 -> 5% is 50 (< 200, so floor 200 applied)
        StatementRequestDto inputDto = new StatementRequestDto();
        inputDto.setCardId("CARD1001");
        inputDto.setStatementDate(LocalDate.now());
        inputDto.setDueDate(LocalDate.now().plusDays(20));
        inputDto.setOpeningBalance(BigDecimal.ZERO);
        inputDto.setTotalPurchases(new BigDecimal("1000.00"));
        inputDto.setTotalPayments(BigDecimal.ZERO);
        inputDto.setTotalRefunds(BigDecimal.ZERO);
        inputDto.setTotalFees(BigDecimal.ZERO);
        inputDto.setTotalInterest(BigDecimal.ZERO);

        when(cardRepository.existsById("CARD1001")).thenReturn(true);
        when(statementRepository.existsById(any())).thenReturn(false);
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StatementResponseDto result = statementService.addStatement(inputDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getClosingBalance());
        // 5% of 1000 is 50, but floor of 200 should apply because closingBalance > 0
        assertEquals(new BigDecimal("200"), result.getMinimumDue());
    }

    @Test
    void testAddStatement_MinimumDueCalculation_AboveFloor() {
        // Closing: 25000 + 40000 - 20000 - 3000 + 1000 = 43000 -> 5% is 2150.00
        StatementRequestDto inputDto = new StatementRequestDto();
        inputDto.setCardId("CARD1001");
        inputDto.setStatementDate(LocalDate.now());
        inputDto.setDueDate(LocalDate.now().plusDays(20));
        inputDto.setOpeningBalance(new BigDecimal("25000.00"));
        inputDto.setTotalPurchases(new BigDecimal("40000.00"));
        inputDto.setTotalPayments(new BigDecimal("20000.00"));
        inputDto.setTotalRefunds(new BigDecimal("3000.00"));
        inputDto.setTotalFees(BigDecimal.ZERO);
        inputDto.setTotalInterest(new BigDecimal("1000.00"));

        when(cardRepository.existsById("CARD1001")).thenReturn(true);
        when(statementRepository.existsById(any())).thenReturn(false);
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StatementResponseDto result = statementService.addStatement(inputDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("43000.00"), result.getClosingBalance());
        assertEquals(new BigDecimal("2150.0000"), result.getMinimumDue());
    }

    @Test
    void testAddStatement_ZeroBalance_MinimumDueZero() {
        StatementRequestDto inputDto = new StatementRequestDto();
        inputDto.setCardId("CARD1001");
        inputDto.setStatementDate(LocalDate.now());
        inputDto.setDueDate(LocalDate.now().plusDays(20));
        inputDto.setOpeningBalance(BigDecimal.ZERO);
        inputDto.setTotalPurchases(BigDecimal.ZERO);
        inputDto.setTotalPayments(BigDecimal.ZERO);
        inputDto.setTotalRefunds(BigDecimal.ZERO);
        inputDto.setTotalFees(BigDecimal.ZERO);
        inputDto.setTotalInterest(BigDecimal.ZERO);

        when(cardRepository.existsById("CARD1001")).thenReturn(true);
        when(statementRepository.existsById(any())).thenReturn(false);
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StatementResponseDto result = statementService.addStatement(inputDto);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getClosingBalance());
        assertEquals(BigDecimal.ZERO, result.getMinimumDue());
    }
}
