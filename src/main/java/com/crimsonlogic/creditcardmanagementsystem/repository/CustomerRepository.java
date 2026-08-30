package com.crimsonlogic.creditcardmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

}