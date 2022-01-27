package com.splunk.rum.demoApp.util;

@SuppressWarnings("ALL")
public final class AppConstant {

    // Defind all comman constants
    public static final String ERROR_UNKNOWN = "ERR0001";
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int REQUEST_CHECK_SETTINGS = 35;
    public static final int SPLASH_SCREEN_DURATION = 3000;
    public static final String PRODUCT_JSON_FILE_NAME = "product.json";
    public static final String FAKE_CC_NUMBER = "0000-0000-0000-0000";

    @SuppressWarnings("unused")
    // Defind all the dialog identifier constants
    public static final class DialogIdentifier {
        public static final int EMPTY_CART = 2;
        public static final int EXIT = 3;
    }

    // Defind all the intent key constants
    public static final class IntentKey {
        public static final String IS_FROM_PRODUCT_DETAIL = "IS_FROM_PRODUCT_DETAIL";
        public static final String PRODUCT_DETAILS = "PRODUCT_DETAILS";
        public static final String PRODUCT_ARRAY = "PRODUCT_ARRAY";
        public static final String IS_FROM_PRODUCT_ITEM = "IS_FROM_PRODUCT_ITEM";
    }

    // Defind all the shared pref key constants
    public static final class SharedPrefKey {
        public static final String CART_PRODUCTS = "CART_PRODUCTS";
    }

    // Defind all the form data parameter constants
    public static final class FormDataParameter{
        public static final String QUANTITY = "quantity";
        public static final String PRODUCT_ID = "product_id";
        public static final String EMAIL = "email";
        public static final String ADDRESS = "street_address";
        public static final String ZIPCODE = "zip_code";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String COUNTRY = "country";
        public static final String CC_NUMBER = "credit_card_number";
        public static final String CC_EX_MONTH = "credit_card_expiration_month";
        public static final String CC_EX_YEAR = "credit_card_expiration_year";
        public static final String CC_CVV = "credit_card_cvv";
    }

    public static final class ErrorType{
        public static final String ERR_4XX = "4xx";
        public static final String ERR_5XX = "5xx";
        public static final String ERR_EXCEPTION = "Exception";
        public static final String ERR_CRASH = "crash";
        public static final String ERR_FREEZE = "Freeze";
        public static final String ERR_ANR = "ANR";
    }

    public static final class ErrorAction{
        public static final String ACTION_CART = "cart";
        public static final String ACTION_ADD_PRODUCT = "Add Product";
        public static final String ACTION_VIEW = "view";
    }

}
