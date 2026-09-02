package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentDto;

import java.util.List;

public interface IPaymentService {

    PaymentDto addPayment(PaymentDto paymentDto);

    PaymentDto getPaymentById(String paymentId);

    List<PaymentDto> getPaymentsByCustomerId(String customerId);

    List<PaymentDto> getPaymentsByCardId(String cardId);

    List<PaymentDto> getAllPayments();
}
