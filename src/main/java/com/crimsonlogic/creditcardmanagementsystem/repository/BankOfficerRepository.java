package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankOfficerRepository extends JpaRepository<BankOfficer, String> {
    Optional<BankOfficer> findByUserId(String userId);
}
