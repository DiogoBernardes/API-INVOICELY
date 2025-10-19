package com.api.invoicely.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class ValidationUtils {


    public static boolean isValidPortugueseNif(String nif) {
        if (nif == null || !nif.matches("\\d{9}")) return false;
        int total = 0;
        for (int i = 0; i < 8; i++) {
            total += Character.getNumericValue(nif.charAt(i)) * (9 - i);
        }
        int checkDigit = 11 - (total % 11);
        if (checkDigit >= 10) checkDigit = 0;
        return checkDigit == Character.getNumericValue(nif.charAt(8));
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
