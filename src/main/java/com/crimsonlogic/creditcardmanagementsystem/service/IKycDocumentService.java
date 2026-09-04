package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentResponseDto;

import java.util.List;

public interface IKycDocumentService {

    KycDocumentResponseDto submitDocument(KycDocumentRequestDto requestDto);

    KycDocumentResponseDto getDocumentById(String kycDocumentId);

    List<KycDocumentResponseDto> getAllDocuments();

    List<KycDocumentResponseDto> getDocumentsByCustomerId(String customerId);

    KycDocumentResponseDto verifyDocument(String kycDocumentId, String verifiedByStaffId);

    KycDocumentResponseDto rejectDocument(String kycDocumentId, String verifiedByStaffId, String rejectionReason);

    void deleteDocument(String kycDocumentId);
}
