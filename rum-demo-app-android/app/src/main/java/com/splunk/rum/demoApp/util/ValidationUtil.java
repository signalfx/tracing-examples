package com.splunk.rum.demoApp.util;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

    /**
     * @param email The email address string.
     * @return input email address is valid or not(true/false)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidEmail(String email) {
        if (StringHelper.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }


    /**
     * @param creditCard The credit card number string
     * @return input credit card number is valid or not according to regex defined in method(true/false)
     */
    public static boolean isValidCard(String creditCard) {

        String newCreditCard = creditCard.replaceAll("-", "");

        String regex = "^[0-9]{16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(newCreditCard);
        return matcher.matches();
    }

    /**
     * @param cvv The cvv number string
     * @return cvv number is valid or not according to regex defined in method(true/false)
     */
    public static boolean isValidCvv(String cvv) {
        String regex = "^[0-9]{3}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cvv);
        return matcher.matches();
    }

    /**
     * @param zipCode The zip code number string
     * @return zip code number is valid or not according to regex defined in method(true/false)
     */
    public static boolean isValidZipCode(String zipCode) {
        String regex = "^[0-9]{4,5}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(zipCode);
        return matcher.matches();
    }

    /**
     * @param viewEditText Edittext in which error message show
     * @param viewInputTextLout InputTextLayout in which error message show
     * @param message Error message which we want to show below the edit text
     */
    public static void setErrorIntoInputTextLayout(View viewEditText, View viewInputTextLout, String message) {
        if (!StringHelper.isEmpty(message)) {
            ((TextInputLayout) viewInputTextLout).setError(message);
            (viewEditText).requestFocus();
        }
    }

    /**
     * @param viewInputTextLout InputTextLayout in which we want to remove the error message
     */
    public static void removeErrorFromTextLayout(TextInputLayout viewInputTextLout) {
        if (viewInputTextLout != null) {
            viewInputTextLout.setError("");
        }
    }

}