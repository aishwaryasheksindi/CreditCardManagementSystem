package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;
import com.crimsonlogic.creditcardmanagementsystem.entity.*;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.repository.*;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceAgentRoleTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private DisputeRepository disputeRepository;

    @Mock
    private KycDocumentRepository kycDocumentRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private RiskScoreRepository riskScoreRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IAuditLogService auditLogService;

    @Mock
    private CurrentUserContext currentUserContext;

    @BeforeEach
    void setUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "sneha_csa", "password", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER_SERVICE_AGENT")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCsaCustomerSearch_PermittedAcrossAllBranches() {
        when(currentUserContext.isBankOfficer()).thenReturn(false);

        Customer c1 = new Customer();
        c1.setCustomerId("CUST1001");
        c1.setName("Alice Sharma");
        c1.setBranchCode("CN8080");

        Customer c2 = new Customer();
        c2.setCustomerId("CUST1002");
        c2.setName("Bob Verma");
        c2.setBranchCode("CN4200");

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        CustomerServiceImpl customerService = new CustomerServiceImpl(
                customerRepository, null, null, null, auditLogService, currentUserContext
        );

        List<CustomerResponseDto> results = customerService.searchCustomers(null, null, null);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("CN8080", results.get(0).getBranchCode());
        assertEquals("CN4200", results.get(1).getBranchCode());
    }

    @Test
    void testCsaGetCustomerById_PermittedAcrossBranches() {
        Customer c1 = new Customer();
        c1.setCustomerId("CUST1001");
        c1.setName("Alice Sharma");
        c1.setBranchCode("CN8080");

        when(customerRepository.findById("CUST1001")).thenReturn(Optional.of(c1));
        doNothing().when(currentUserContext).assertCustomerBranchAccess(c1);

        CustomerServiceImpl customerService = new CustomerServiceImpl(
                customerRepository, null, null, null, auditLogService, currentUserContext
        );

        CustomerResponseDto result = customerService.getCustomerById("CUST1001");
        assertNotNull(result);
        assertEquals("CUST1001", result.getCustomerId());
        verify(currentUserContext).assertCustomerBranchAccess(c1);
    }

    @Test
    void testCsaKycVerify_ThrowsAccessDeniedException() {
        KycDocumentServiceImpl kycService = new KycDocumentServiceImpl(
                kycDocumentRepository, customerRepository, auditLogService, staffRepository, currentUserContext
        );

        when(currentUserContext.getCurrentUserId()).thenReturn("USR_CSA_1");
        CustomerServiceAgent csa = new CustomerServiceAgent();
        csa.setStaffId("CSA1001");
        csa.setUserId("USR_CSA_1");
        when(staffRepository.findByUserId("USR_CSA_1")).thenReturn(Optional.of(csa));

        KycDocument doc = new KycDocument();
        doc.setKycDocumentId("KYC1001");
        doc.setCustomerId("CUST1001");
        when(kycDocumentRepository.findById("KYC1001")).thenReturn(Optional.of(doc));

        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setBranchCode("CN8080");
        when(customerRepository.findById("CUST1001")).thenReturn(Optional.of(customer));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                kycService.verifyDocument("KYC1001")
        );
        assertTrue(ex.getMessage().contains("Only Admin or Bank Officer"));
    }

    @Test
    void testCsaFraudAlerts_ReturnsEmptyListAndRejectsDetails() {
        when(currentUserContext.isCustomerServiceAgent()).thenReturn(true);

        FraudAlertServiceImpl fraudAlertService = new FraudAlertServiceImpl(
                fraudAlertRepository, transactionRepository, riskScoreRepository, staffRepository, currentUserContext
        );

        List<FraudAlertResponseDto> alerts = fraudAlertService.getAllFraudAlerts();
        assertTrue(alerts.isEmpty());

        assertThrows(AccessDeniedException.class, () ->
                fraudAlertService.getFraudAlertById("FA1001")
        );
    }

    @Test
    void testCsaRiskScores_ReturnsEmptyListAndRejectsDetails() {
        when(currentUserContext.isCustomerServiceAgent()).thenReturn(true);

        RiskScoreServiceImpl riskScoreService = new RiskScoreServiceImpl(
                riskScoreRepository, transactionRepository, currentUserContext
        );

        List<RiskScoreResponseDto> scores = riskScoreService.getAllRiskScores();
        assertTrue(scores.isEmpty());

        assertThrows(AccessDeniedException.class, () ->
                riskScoreService.getRiskScoreById("RS1001")
        );
    }

    @Test
    void testSensitiveCardCredentials_NotExposedInDto() {
        CardResponseDto dto = new CardResponseDto();
        dto.setCardId("CARD1001");
        dto.setCardReference("CRD-XXXX-1234");
        dto.setCustomerId("CUST1001");

        // Verify that CardResponseDto class has no pin, pinHash, or cvv getters
        boolean hasPin = false;
        boolean hasCvv = false;
        for (java.lang.reflect.Method m : CardResponseDto.class.getDeclaredMethods()) {
            String name = m.getName().toLowerCase();
            if (name.contains("pin") || name.contains("cvv")) {
                hasPin = true;
                break;
            }
        }
        assertFalse(hasPin, "CardResponseDto must not expose PIN or CVV methods");
    }
}
