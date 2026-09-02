package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementRepository extends JpaRepository<Statement, String> {
    List<Statement> findByCardId(String cardId);
}
