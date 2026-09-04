package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.EmiPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmiPlanRepository extends JpaRepository<EmiPlan, String> {

    List<EmiPlan> findByTransactionId(String transactionId);

    List<EmiPlan> findByStatus(String status);
}
