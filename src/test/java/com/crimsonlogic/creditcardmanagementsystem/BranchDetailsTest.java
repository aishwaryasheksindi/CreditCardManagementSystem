package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.utility.BranchDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchDetailsTest {

    @Test
    void testBranchConstantsAndHelpers() {
        assertEquals("CN8080", BranchDetails.BRANCH_CODE_KORAMANGALA);
        assertEquals("CardNest - Koramangala Branch", BranchDetails.BRANCH_NAME_KORAMANGALA);
        assertEquals("80 Feet Road, Koramangala, Bengaluru", BranchDetails.BRANCH_ADDRESS_KORAMANGALA);

        assertEquals("CN4200", BranchDetails.BRANCH_CODE_INDIRANAGAR);
        assertEquals("CardNest - Indiranagar Branch", BranchDetails.BRANCH_NAME_INDIRANAGAR);
        assertEquals("100 Feet Road, Indiranagar, Bengaluru", BranchDetails.BRANCH_ADDRESS_INDIRANAGAR);

        assertTrue(BranchDetails.isValidBranchCode("CN8080"));
        assertTrue(BranchDetails.isValidBranchCode("CN4200"));
        assertFalse(BranchDetails.isValidBranchCode("CN0000"));
        assertFalse(BranchDetails.isValidBranchCode(""));
        assertFalse(BranchDetails.isValidBranchCode(null));

        assertEquals("CardNest - Koramangala Branch", BranchDetails.getBranchName("CN8080"));
        assertEquals("CardNest - Indiranagar Branch", BranchDetails.getBranchName("CN4200"));
        assertNull(BranchDetails.getBranchName("INVALID"));
        assertNull(BranchDetails.getBranchName(null));

        assertEquals("80 Feet Road, Koramangala, Bengaluru", BranchDetails.getBranchAddress("CN8080"));
        assertEquals("100 Feet Road, Indiranagar, Bengaluru", BranchDetails.getBranchAddress("CN4200"));
        assertNull(BranchDetails.getBranchAddress("INVALID"));
        assertNull(BranchDetails.getBranchAddress(null));

        assertEquals(2, BranchDetails.getAllBranchCodes().size());
        assertTrue(BranchDetails.getAllBranchCodes().contains("CN8080"));
        assertTrue(BranchDetails.getAllBranchCodes().contains("CN4200"));
    }
}
