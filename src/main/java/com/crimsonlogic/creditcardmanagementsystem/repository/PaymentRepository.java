package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByCustomerId(String customerId);
    List<Payment> findByCardId(String cardId);
}
