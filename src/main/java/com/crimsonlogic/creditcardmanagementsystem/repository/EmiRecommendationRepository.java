package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.EmiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmiRecommendationRepository extends JpaRepository<EmiRecommendation, String> {

    List<EmiRecommendation> findByTransactionId(String transactionId);
}
