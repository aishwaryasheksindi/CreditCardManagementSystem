package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.RewardRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRecommendationRepository extends JpaRepository<RewardRecommendation, String> {

    List<RewardRecommendation> findByCustomerId(String customerId);
}
