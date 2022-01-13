package com.splunk.rum.demoApp.util;

import androidx.annotation.Nullable;


public final class StringHelper {

    /**
     * Check String is Empty OR Null
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }


}
