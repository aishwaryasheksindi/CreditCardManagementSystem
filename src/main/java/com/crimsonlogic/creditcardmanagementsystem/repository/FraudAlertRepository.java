package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, String> {

    List<FraudAlert> findByTransactionId(String transactionId);

    List<FraudAlert> findByStatus(String status);

    List<FraudAlert> findByInvestigatorStaffId(String investigatorStaffId);
}
