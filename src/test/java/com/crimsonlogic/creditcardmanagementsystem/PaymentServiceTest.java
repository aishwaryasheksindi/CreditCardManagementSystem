package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Payment;
import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.PaymentRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void testAddPayment_Success_UpdatesCardAvailableLimit() {
        PaymentRequestDto paymentDto = new PaymentRequestDto();
        paymentDto.setCardId("CARD1001");
        paymentDto.setCustomerId("CUST1001");
        paymentDto.setAmount(new BigDecimal("5000.00"));
        paymentDto.setPaymentDate(LocalDateTime.now());
        paymentDto.setPaymentType("FULL");
        paymentDto.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentDto.setPaymentMethod("NET_BANKING");

        Card card = new Card();
        card.setCardId("CARD1001");
        card.setCreditLimit(new BigDecimal("100000.00"));
        card.setAvailableLimit(new BigDecimal("40000.00"));

        when(cardRepository.findById("CARD1001")).thenReturn(Optional.of(card));
        when(customerRepository.existsById("CUST1001")).thenReturn(true);
        when(paymentRepository.existsById(any())).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponseDto result = paymentService.addPayment(paymentDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
        assertEquals(new BigDecimal("45000.00"), card.getAvailableLimit());
        verify(cardRepository).save(card);
    }
}
