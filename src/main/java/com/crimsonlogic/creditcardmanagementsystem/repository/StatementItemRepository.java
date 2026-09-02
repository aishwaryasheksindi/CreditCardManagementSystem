package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.StatementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementItemRepository extends JpaRepository<StatementItem, String> {
    List<StatementItem> findByStatementId(String statementId);
}
