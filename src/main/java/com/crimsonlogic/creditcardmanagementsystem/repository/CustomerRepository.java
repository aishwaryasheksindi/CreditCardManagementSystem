package com.crimsonlogic.creditcardmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findByNameContainingIgnoreCase(String name);

    List<Customer> findByPhoneNumber(String phoneNumber);

    List<Customer> findByEmail(String email);
}