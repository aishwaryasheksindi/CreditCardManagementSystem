package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.enums.DocumentType;
import com.crimsonlogic.creditcardmanagementsystem.utility.DocumentValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentValidationUtilTest {

    @Test
    void testAadhaarValidation() {
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "123456789012"));

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "12345678901")); // 11 digits
        assertTrue(ex1.getMessage().contains("Invalid AADHAAR number format"));

        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "1234567890123")); // 13 digits
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "12345678901A")); // non-digit
    }

    @Test
    void testPanValidation() {
        // Lowercase PAN should pass because utility normalizes to uppercase
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.PAN, "abcde1234f"));
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.PAN, "ABCDE1234F"));

        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.PAN, "ABCDE12345"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.PAN, "12345ABCDE"));
    }

    @Test
    void testPassportValidation() {
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "A1234567"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "12345678"));
    }

    @Test
    void testVoterIdValidation() {
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.VOTER_ID, "ABC1234567"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.VOTER_ID, "AB12345678"));
    }

    @Test
    void testDrivingLicenseValidation() {
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.DRIVING_LICENSE, "DL0123456789012"));
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.DRIVING_LICENSE, "DL01234567890123"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.DRIVING_LICENSE, "D1234567890123"));
    }

    @Test
    void testNullOrEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(null, "123456789012"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, null));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "   "));
    }
}
