package com.crimsonlogic.creditcardmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}