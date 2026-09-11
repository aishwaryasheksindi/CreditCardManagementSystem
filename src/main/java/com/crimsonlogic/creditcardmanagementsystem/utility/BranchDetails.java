package com.crimsonlogic.creditcardmanagementsystem.utility;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class BranchDetails {

    public static final String BRANCH_CODE_KORAMANGALA = "CN8080";
    public static final String BRANCH_NAME_KORAMANGALA = "CardNest - Koramangala Branch";
    public static final String BRANCH_ADDRESS_KORAMANGALA = "80 Feet Road, Koramangala, Bengaluru";

    public static final String BRANCH_CODE_INDIRANAGAR = "CN4200";
    public static final String BRANCH_NAME_INDIRANAGAR = "CardNest - Indiranagar Branch";
    public static final String BRANCH_ADDRESS_INDIRANAGAR = "100 Feet Road, Indiranagar, Bengaluru";

    private static final Map<String, String> BRANCH_NAMES = new LinkedHashMap<>();
    private static final Map<String, String> BRANCH_ADDRESSES = new LinkedHashMap<>();

    static {
        BRANCH_NAMES.put(BRANCH_CODE_KORAMANGALA, BRANCH_NAME_KORAMANGALA);
        BRANCH_NAMES.put(BRANCH_CODE_INDIRANAGAR, BRANCH_NAME_INDIRANAGAR);

        BRANCH_ADDRESSES.put(BRANCH_CODE_KORAMANGALA, BRANCH_ADDRESS_KORAMANGALA);
        BRANCH_ADDRESSES.put(BRANCH_CODE_INDIRANAGAR, BRANCH_ADDRESS_INDIRANAGAR);
    }

    private BranchDetails() {
    }

    public static boolean isValidBranchCode(String branchCode) {
        return branchCode != null && BRANCH_NAMES.containsKey(branchCode.trim());
    }

    public static String getBranchName(String branchCode) {
        if (branchCode == null) {
            return null;
        }
        return BRANCH_NAMES.get(branchCode.trim());
    }

    public static String getBranchAddress(String branchCode) {
        if (branchCode == null) {
            return null;
        }
        return BRANCH_ADDRESSES.get(branchCode.trim());
    }

    public static Set<String> getAllBranchCodes() {
        return Collections.unmodifiableSet(BRANCH_NAMES.keySet());
    }
}
