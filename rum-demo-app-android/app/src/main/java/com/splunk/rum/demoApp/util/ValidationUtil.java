package com.splunk.rum.demoApp.util;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidEmail(String email) {
        if (StringHelper.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean isValidCard(String creditCard) {

        String newCreditCard = creditCard.replaceAll("-", "");

        String regex = "^[0-9]{16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(newCreditCard);
        return matcher.matches();
    }

    public static boolean isValidCvv(String cvv) {
        String regex = "^[0-9]{3}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cvv);
        return matcher.matches();
    }

    public static boolean isValidZipCode(String zipCode) {
        String regex = "^[0-9]{4,5}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(zipCode);
        return matcher.matches();
    }

    public static void setErrorIntoInputTextLayout(View viewEditText, View viewInputTextLout, String message) {
        if (!StringHelper.isEmpty(message)) {
            ((TextInputLayout) viewInputTextLout).setError(message);
            (viewEditText).requestFocus();
        }
    }

    public static void removeErrorFromTextLayout(TextInputLayout viewInputTextLout) {
        if (viewInputTextLout != null) {
            viewInputTextLout.setError("");
        }
    }

}