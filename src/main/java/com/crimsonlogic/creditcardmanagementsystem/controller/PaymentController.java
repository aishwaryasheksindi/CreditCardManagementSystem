package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> addPayment(
            @Valid @RequestBody PaymentRequestDto paymentDto) {
        PaymentResponseDto savedPayment = paymentService.addPayment(paymentDto);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @PathVariable String paymentId) {
        PaymentResponseDto paymentDto = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<PaymentResponseDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByCustomerId(
            @PathVariable String customerId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByCardId(
            @PathVariable String cardId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByCardId(cardId);
        return ResponseEntity.ok(payments);
    }
}
