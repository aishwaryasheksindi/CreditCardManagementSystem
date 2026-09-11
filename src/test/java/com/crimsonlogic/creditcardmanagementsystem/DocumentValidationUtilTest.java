package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.enums.DocumentType;
import com.crimsonlogic.creditcardmanagementsystem.utility.DocumentValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentValidationUtilTest {

    @Test
    void testAadhaarValidation() {
        // Scenario 6: Valid 12-digit Aadhaar passes validation
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "123456789012"));
        assertTrue(DocumentValidationUtil.isValidAadhaar("123456789012"));

        // Scenario 7: Invalid length is rejected (11 digits, 13 digits)
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "12345678901")); // 11 digits
        assertTrue(ex1.getMessage().contains("Invalid AADHAAR number format"));
        assertFalse(DocumentValidationUtil.isValidAadhaar("12345678901"));

        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "1234567890123")); // 13 digits
        assertFalse(DocumentValidationUtil.isValidAadhaar("1234567890123"));

        // Scenario 8: Non-numeric Aadhaar is rejected
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "12345678901A")); // non-digit
        assertFalse(DocumentValidationUtil.isValidAadhaar("12345678901A"));
        assertFalse(DocumentValidationUtil.isValidAadhaar("1234abcd5678"));

        // Scenario 9: Obvious repeated-digit Aadhaar such as 111111111111 and 888888888888 is rejected
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "111111111111"));
        assertFalse(DocumentValidationUtil.isValidAadhaar("111111111111"));

        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "888888888888"));
        assertFalse(DocumentValidationUtil.isValidAadhaar("888888888888"));

        // Scenario 10: Obvious excessive repeated pattern such as 888888999999 is rejected
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.AADHAAR, "888888999999"));
        assertFalse(DocumentValidationUtil.isValidAadhaar("888888999999"));
    }

    @Test
    void testPassportValidation() {
        // Scenario 11: Valid passport such as A1234567 passes validation
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "A1234567"));
        assertDoesNotThrow(() -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "Z9876543"));
        assertTrue(DocumentValidationUtil.isValidPassport("A1234567"));

        // Scenario 12: Invalid passport format is rejected
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "12345678"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "AB123456"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "A123456"));
        assertThrows(IllegalArgumentException.class,
                () -> DocumentValidationUtil.validate(DocumentType.PASSPORT, "A12345678"));
        assertFalse(DocumentValidationUtil.isValidPassport("12345678"));
        assertFalse(DocumentValidationUtil.isValidPassport("AB123456"));
        assertFalse(DocumentValidationUtil.isValidPassport("A123456"));
        assertFalse(DocumentValidationUtil.isValidPassport("A12345678"));
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
