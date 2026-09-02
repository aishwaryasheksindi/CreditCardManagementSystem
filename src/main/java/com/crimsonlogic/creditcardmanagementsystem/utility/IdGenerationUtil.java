package com.crimsonlogic.creditcardmanagementsystem.utility;

import java.util.Random;

public class IdGenerationUtil {

    private static final Random random = new Random();

    private IdGenerationUtil() {
    }

    private static String generateNumber(int digits) {

        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits) - 1;

        int number = random.nextInt(max - min + 1) + min;

        return String.valueOf(number);
    }

    public static String generateCustomerId() {
        return "CUST" + generateNumber(4);
    }

    public static String generateUserId() {
        return "USR" + generateNumber(4);
    }

    public static String generateRoleId() {
        return "ROLE" + generateNumber(4);
    }

    public static String generateCardId() {
        return "CARD" + generateNumber(4);
    }

    public static String generateCardTypeId() {
        return "CT" + generateNumber(4);
    }

    public static String generateCardStatusHistoryId() {
        return "CSH" + generateNumber(4);
    }

    public static String generateMerchantId() {
        return "MER" + generateNumber(4);
    }

    public static String generateTransactionId() {
        return "TXN" + generateNumber(6);
    }

    public static String generateCategoryId() {
        return "CAT" + generateNumber(4);
    }

    public static String generateStatementId() {
        return "STMT" + generateNumber(6);
    }

    public static String generateStatementItemId() {
        return "STI" + generateNumber(6);
    }

    public static String generatePaymentId() {
        return "PAY" + generateNumber(6);
    }

    public static String generateStaffId() {
        return "STF" + generateNumber(4);
    }
}