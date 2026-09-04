package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.SpendingInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpendingInsightRepository extends JpaRepository<SpendingInsight, String> {

    List<SpendingInsight> findByCustomerId(String customerId);
}
