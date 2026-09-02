package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.PaymentDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.Payment;
import com.crimsonlogic.creditcardmanagementsystem.enums.PaymentStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.PaymentRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              CardRepository cardRepository,
                              CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.cardRepository = cardRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public PaymentDto addPayment(PaymentDto paymentDto) {

        Card card = cardRepository.findById(paymentDto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + paymentDto.getCardId()));

        if (!customerRepository.existsById(paymentDto.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with ID: " + paymentDto.getCustomerId());
        }

        String paymentId;
        do {
            paymentId = IdGenerationUtil.generatePaymentId();
        } while (paymentRepository.existsById(paymentId));

        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setCardId(paymentDto.getCardId());
        payment.setCustomerId(paymentDto.getCustomerId());
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setPaymentType(paymentDto.getPaymentType().toUpperCase());
        payment.setPaymentStatus(paymentDto.getPaymentStatus());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setReferenceNumber(paymentDto.getReferenceNumber());

        // Update card available limit transactionally upon successful payment per PDF §16
        if (paymentDto.getPaymentStatus() == PaymentStatus.SUCCESS) {
            BigDecimal newAvailableLimit = card.getAvailableLimit().add(paymentDto.getAmount());
            if (newAvailableLimit.compareTo(card.getCreditLimit()) > 0) {
                newAvailableLimit = card.getCreditLimit();
            }
            card.setAvailableLimit(newAvailableLimit);
            cardRepository.save(card);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDto(savedPayment);
    }

    @Override
    public PaymentDto getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return convertToDto(payment);
    }

    @Override
    public List<PaymentDto> getPaymentsByCustomerId(String customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getPaymentsByCardId(String cardId) {
        return paymentRepository.findByCardId(cardId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setCardId(payment.getCardId());
        dto.setCustomerId(payment.getCustomerId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentType(payment.getPaymentType());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setReferenceNumber(payment.getReferenceNumber());
        return dto;
    }
}
