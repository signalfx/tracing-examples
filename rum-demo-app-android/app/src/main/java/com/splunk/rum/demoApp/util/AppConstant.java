package com.splunk.rum.demoApp.util;

@SuppressWarnings("ALL")
public final class AppConstant {

    public static final String ERROR_UNKNOWN = "ERR0001";
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int REQUEST_LOCATION_CODE = 35;
    public static final int SPLASH_SCREEN_DURATION = 3000;
    public static final String PRODUCT_JSON_FILE_NAME = "product.json";

    @SuppressWarnings("unused")
    public static final class DialogIdentifier {
        public static final int EMPTY_CART = 2;
        public static final int EXIT = 3;
    }

    public static final class IntentKey {
        public static final String IS_FROM_PRODUCT_DETAIL = "IS_FROM_PRODUCT_DETAIL";
        public static final String PRODUCT_DETAILS = "PRODUCT_DETAILS";
        public static final String PRODUCT_ARRAY = "PRODUCT_ARRAY";
    }


    public static final class SharedPrefKey {
        public static final String CART_PRODUCTS = "CART_PRODUCTS";
    }
}
