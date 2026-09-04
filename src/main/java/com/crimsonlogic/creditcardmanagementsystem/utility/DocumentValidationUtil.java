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
            case AADHAAR -> value.matches("^\\d{12}$");
            case PAN -> value.matches("^[A-Z]{5}[0-9]{4}[A-Z]$");
            case PASSPORT -> value.matches("^[A-Z][0-9]{7}$"); // Indian passport format
            case VOTER_ID -> value.matches("^[A-Z]{3}[0-9]{7}$"); // EPIC format
            case DRIVING_LICENSE -> value.matches("^[A-Z]{2}[0-9]{13,14}$"); // simplified DL format
        };
        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid " + type + " number format: " + documentNumber);
        }
    }
}
