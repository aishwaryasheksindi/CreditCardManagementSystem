package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.KycDocument;
import com.crimsonlogic.creditcardmanagementsystem.entity.Staff;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.DocumentType;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.KycDocumentRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StaffRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
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

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private CurrentUserContext currentUserContext;

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

    @Test
    void testVerifyDocument_Success() {
        String docId = "DOC1001";
        String actingUserId = "USR_STAFF_1";
        String staffId = "STAFF_007";
        String customerId = "CUST101";

        Staff staff = new BankOfficer();
        staff.setStaffId(staffId);
        staff.setUserId(actingUserId);

        KycDocument document = new KycDocument();
        document.setKycDocumentId(docId);
        document.setCustomerId(customerId);
        document.setDocumentType(DocumentType.PAN);
        document.setDocumentNumber("ABCDE1234F");
        document.setStatus(KycStatus.PENDING);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setKycStatus(KycStatus.PENDING);

        when(currentUserContext.getCurrentUserId()).thenReturn(actingUserId);
        when(staffRepository.findByUserId(actingUserId)).thenReturn(Optional.of(staff));
        when(kycDocumentRepository.findById(docId)).thenReturn(Optional.of(document));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(kycDocumentRepository.save(any(KycDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KycDocumentResponseDto response = kycDocumentService.verifyDocument(docId);

        assertNotNull(response);
        assertEquals(KycStatus.VERIFIED, response.getStatus());
        assertEquals(staffId, response.getVerifiedByStaffId());
        assertEquals(KycStatus.VERIFIED, customer.getKycStatus());

        verify(customerRepository).save(customer);
        verify(auditLogService).logAction(eq(staffId), eq(AuditAction.STATUS_CHANGE), eq("KycDocument"), eq(docId), contains("VERIFIED"));
    }

    @Test
    void testRejectDocument_Success() {
        String docId = "DOC1001";
        String actingUserId = "USR_STAFF_1";
        String staffId = "STAFF_007";
        String customerId = "CUST101";
        String rejectionReason = "Document image is blurred and unreadable";

        Staff staff = new BankOfficer();
        staff.setStaffId(staffId);
        staff.setUserId(actingUserId);

        KycDocument document = new KycDocument();
        document.setKycDocumentId(docId);
        document.setCustomerId(customerId);
        document.setDocumentType(DocumentType.PAN);
        document.setDocumentNumber("ABCDE1234F");
        document.setStatus(KycStatus.PENDING);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setKycStatus(KycStatus.PENDING);

        when(currentUserContext.getCurrentUserId()).thenReturn(actingUserId);
        when(staffRepository.findByUserId(actingUserId)).thenReturn(Optional.of(staff));
        when(kycDocumentRepository.findById(docId)).thenReturn(Optional.of(document));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(kycDocumentRepository.save(any(KycDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KycDocumentResponseDto response = kycDocumentService.rejectDocument(docId, rejectionReason);

        assertNotNull(response);
        assertEquals(KycStatus.REJECTED, response.getStatus());
        assertEquals(staffId, response.getVerifiedByStaffId());
        assertEquals(rejectionReason, response.getRejectionReason());
        assertEquals(KycStatus.REJECTED, customer.getKycStatus());

        verify(customerRepository).save(customer);
        verify(auditLogService).logAction(eq(staffId), eq(AuditAction.STATUS_CHANGE), eq("KycDocument"), eq(docId), contains("REJECTED"));
    }

    @Test
    void testVerifyDocument_StaffNotFound_ThrowsResourceNotFoundException() {
        String docId = "DOC1001";
        String actingUserId = "USR_NON_STAFF";

        when(currentUserContext.getCurrentUserId()).thenReturn(actingUserId);
        when(staffRepository.findByUserId(actingUserId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            kycDocumentService.verifyDocument(docId);
        });

        assertTrue(ex.getMessage().contains("No staff record found for the authenticated user: " + actingUserId));
        verify(kycDocumentRepository, never()).save(any());
        verify(customerRepository, never()).save(any());
    }
}
