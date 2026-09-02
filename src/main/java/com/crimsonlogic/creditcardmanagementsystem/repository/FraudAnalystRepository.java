package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.FraudAnalyst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudAnalystRepository extends JpaRepository<FraudAnalyst, String> {
    Optional<FraudAnalyst> findByUserId(String userId);
}
