package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentResponseDto;

import java.util.List;

public interface IPaymentService {

    PaymentResponseDto addPayment(PaymentRequestDto paymentDto);

    PaymentResponseDto getPaymentById(String paymentId);

    List<PaymentResponseDto> getPaymentsByCustomerId(String customerId);

    List<PaymentResponseDto> getPaymentsByCardId(String cardId);

    List<PaymentResponseDto> getAllPayments();
}
