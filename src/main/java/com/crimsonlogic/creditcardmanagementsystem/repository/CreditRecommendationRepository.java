package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.CreditRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRecommendationRepository extends JpaRepository<CreditRecommendation, String> {

    List<CreditRecommendation> findByCustomerId(String customerId);
}
