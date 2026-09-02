package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentDto;
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
    public ResponseEntity<PaymentDto> addPayment(
            @Valid @RequestBody PaymentDto paymentDto) {
        PaymentDto savedPayment = paymentService.addPayment(paymentDto);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentById(
            @PathVariable String paymentId) {
        PaymentDto paymentDto = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByCustomerId(
            @PathVariable String customerId) {
        List<PaymentDto> payments = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByCardId(
            @PathVariable String cardId) {
        List<PaymentDto> payments = paymentService.getPaymentsByCardId(cardId);
        return ResponseEntity.ok(payments);
    }
}
