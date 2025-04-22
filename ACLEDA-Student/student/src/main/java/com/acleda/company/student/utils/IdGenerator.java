package com.acleda.company.student.utils;

import java.util.Random;

public class IdGenerator {

    // PaymentInfoId = 9
    public static String generatePaymentInfoId() {
        return generateSaltString(9);
    }

    public static String generateSequenceMakeReverseTransaction() {
        return generateSaltString(13);
    }

    // TransactionRefNo 9 Digits Random + PaymentInfoId 9 Digits
    public static String generateTransactionRefNo(String previousPaymentInfoId) {
        return generateSaltString(9) + previousPaymentInfoId;
    }

    private static String generateSaltString(int digit) {
        String strSALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < digit) { // length of the random string.
            int index = (int) (rnd.nextFloat() * strSALTCHARS.length());
            salt.append(strSALTCHARS.charAt(index));
        }
        return salt.toString();
    }
}
