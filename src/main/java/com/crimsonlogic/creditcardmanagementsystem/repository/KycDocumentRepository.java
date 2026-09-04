package com.crimsonlogic.creditcardmanagementsystem.repository;

import com.crimsonlogic.creditcardmanagementsystem.entity.KycDocument;
import com.crimsonlogic.creditcardmanagementsystem.enums.DocumentType;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, String> {
    List<KycDocument> findByCustomerId(String customerId);

    Optional<KycDocument> findByDocumentTypeAndDocumentNumberAndStatus(DocumentType documentType, String documentNumber, KycStatus status);
}
