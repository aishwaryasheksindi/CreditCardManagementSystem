package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.KycDocument;
import com.crimsonlogic.creditcardmanagementsystem.enums.DocumentType;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.KycDocumentRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import com.crimsonlogic.creditcardmanagementsystem.service.KycDocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KycDocumentServiceTest {

    @Mock
    private KycDocumentRepository kycDocumentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IAuditLogService auditLogService;

    @InjectMocks
    private KycDocumentServiceImpl kycDocumentService;

    @Test
    void testSubmitDocument_ValidAadhaar_Success() {
        KycDocumentRequestDto request = new KycDocumentRequestDto();
        request.setCustomerId("CUST101");
        request.setDocumentType(DocumentType.AADHAAR);
        request.setDocumentNumber("123456789012");
        request.setDocumentUrl("https://storage.example.com/aadhaar.pdf");

        when(customerRepository.existsById("CUST101")).thenReturn(true);
        when(kycDocumentRepository.existsById(any())).thenReturn(false);
        when(kycDocumentRepository.findByDocumentTypeAndDocumentNumberAndStatus(DocumentType.AADHAAR, "123456789012", KycStatus.VERIFIED))
                .thenReturn(Optional.empty());
        when(kycDocumentRepository.save(any(KycDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KycDocumentResponseDto response = kycDocumentService.submitDocument(request);

        assertNotNull(response);
        assertEquals(DocumentType.AADHAAR, response.getDocumentType());
        assertEquals("123456789012", response.getDocumentNumber());
        assertEquals(KycStatus.PENDING, response.getStatus());
    }

    @Test
    void testSubmitDocument_InvalidPanFormat_ThrowsIllegalArgumentException() {
        KycDocumentRequestDto request = new KycDocumentRequestDto();
        request.setCustomerId("CUST101");
        request.setDocumentType(DocumentType.PAN);
        request.setDocumentNumber("INVALID_PAN_123");

        when(customerRepository.existsById("CUST101")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            kycDocumentService.submitDocument(request);
        });

        assertTrue(ex.getMessage().contains("Invalid PAN number format"));
        verify(kycDocumentRepository, never()).save(any());
    }

    @Test
    void testSubmitDocument_DuplicateVerifiedDocument_ThrowsDuplicateResourceException() {
        KycDocumentRequestDto request = new KycDocumentRequestDto();
        request.setCustomerId("CUST101");
        request.setDocumentType(DocumentType.PAN);
        request.setDocumentNumber("abcde1234f"); // lowercase to test normalization

        when(customerRepository.existsById("CUST101")).thenReturn(true);
        when(kycDocumentRepository.findByDocumentTypeAndDocumentNumberAndStatus(DocumentType.PAN, "ABCDE1234F", KycStatus.VERIFIED))
                .thenReturn(Optional.of(new KycDocument()));

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            kycDocumentService.submitDocument(request);
        });

        assertTrue(ex.getMessage().contains("is already verified against another customer account"));
        verify(kycDocumentRepository, never()).save(any());
    }
}
