package com.crimsonlogic.creditcardmanagementsystem.utility;

import com.crimsonlogic.creditcardmanagementsystem.enums.DocumentType;

public final class DocumentValidationUtil {

    private DocumentValidationUtil() {
    }

    public static void validate(DocumentType type, String documentNumber) {
        if (type == null) {
            throw new IllegalArgumentException("Document type is required");
        }
        if (documentNumber == null || documentNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Document number is required");
        }
        String value = documentNumber.trim().toUpperCase();
        boolean valid = switch (type) {
            case AADHAAR -> isValidAadhaar(value);
            case PAN -> value.matches("^[A-Z]{5}[0-9]{4}[A-Z]$");
            case PASSPORT -> isValidPassport(value);
            case VOTER_ID -> value.matches("^[A-Z]{3}[0-9]{7}$"); // EPIC format
            case DRIVING_LICENSE -> value.matches("^[A-Z]{2}[0-9]{13,14}$"); // simplified DL format
        };
        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid " + type + " number format: " + documentNumber);
        }
    }

    public static boolean isValidAadhaar(String documentNumber) {
        if (documentNumber == null) {
            return false;
        }
        String value = documentNumber.trim();
        if (!value.matches("^\\d{12}$")) {
            return false;
        }
        // Reject obvious repeated-digit patterns and excessive consecutive repetition of 6 or more identical digits
        return !value.matches(".*(\\d)\\1{5,}.*");
    }

    public static boolean isValidPassport(String documentNumber) {
        if (documentNumber == null) {
            return false;
        }
        String value = documentNumber.trim().toUpperCase();
        return value.matches("^[A-Z][0-9]{7}$");
    }
}
