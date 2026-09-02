package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.CustomerServiceAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerServiceAgentRepository extends JpaRepository<CustomerServiceAgent, String> {
    Optional<CustomerServiceAgent> findByUserId(String userId);
}
