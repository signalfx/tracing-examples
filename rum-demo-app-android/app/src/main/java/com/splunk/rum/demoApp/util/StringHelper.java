package com.splunk.rum.demoApp.util;

import androidx.annotation.Nullable;


public final class StringHelper {


    /**
     * Check string empty or null
     * @param str nullable string
     * @return true/false according to input string
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Check string is not empty
     * @param str nullable string
     * @return true/false according to input string
     */
    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }


}
