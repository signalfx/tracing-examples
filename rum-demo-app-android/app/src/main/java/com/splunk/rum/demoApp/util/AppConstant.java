package com.splunk.rum.demoApp.util;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings("ALL")
public final class AppConstant {

    // Defind all comman constants
    public static final String ERROR_UNKNOWN = "ERR0001";
    public static final String ERROR_INTERNET = "ERR_INTERNET";
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int REQUEST_CHECK_SETTINGS = 35;
    public static final int SPLASH_SCREEN_DURATION = 3000;
    public static final int RUM_CUSTOM_EVENT_2_SEC_DELAY = 2000;
    public static final int RUM_CUSTOM_EVENT_4_SEC_DELAY = 4000;
    public static final String PRODUCT_JSON_FILE_NAME = "product.json";
    public static final String FAKE_CC_NUMBER = "0000-0000-0000-0000";
    public static final String GLOBAL_ATTR_LAT = "_sf_geo_lat";
    public static final String GLOBAL_ATTR_LONG = "_sf_geo_long";
    public static final long LOCATION_INTERVAL = 5000;
    public static final long FASTEST_INTERVAL = 1000;
    public static final int SCREEN_SIZE = 5;
    public static final int SLOW_API_SECOND=4;


    // Defind all the dialog identifier constants
    public static final class DialogIdentifier {
        public static final int EMPTY_CART = 2;
        public static final int EXIT = 3;
        public static final int INTERNET_DIALOG = 4;
        public static final int CHECK_OUT_DIALOG = 5;
    }

    // Defind all the intent key constants
    public static final class IntentKey {
        public static final String IS_FROM_PRODUCT_DETAIL = "IS_FROM_PRODUCT_DETAIL";
        public static final String PRODUCT_DETAILS = "PRODUCT_DETAILS";
        public static final String PRODUCT_ARRAY = "PRODUCT_ARRAY";
        public static final String IS_FROM_PRODUCT_ITEM = "IS_FROM_PRODUCT_ITEM";
        public static final String COUNTRY_NAME = "COUNTRY_NAME";
        public static final String INTENT_KEY = "INTENT_KEY";

    }

    // Defind all the shared pref key constants
    public static final class SharedPrefKey {
        public static final String CART_PRODUCTS = "CART_PRODUCTS";
        public static final String COUNTRY_LIST = "COUNTRY_LIST";
        public static final String TOKEN = "TOKEN";
        public static final String ENVIRONMENT_NAME = "ENVIRONMENT_NAME";
        public static final String RUM_URL = "RUM_URL";
        public static final String REAL_M = "REAL_M";
        public static final String APP_NAME = "APP_NAME";
        public static final String IS_COUNTRY_NAME_EMPTY ="IS_COUNTRY_NAME_NULL";
        public static final String LAT="LAT";
        public static final String LNG="LNG";
        public static final String FRANCE_COORDINATE_JSON="FRANCE_COORDINATE_JSON";

    }

    // Defind all the form data parameter constants
    public static final class FormDataParameter {
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

    public static final class ErrorType {
        public static final String ERR_4XX = "4xx";
        public static final String ERR_5XX = "5xx";
        public static final String ERR_EXCEPTION = "Exception";
        public static final String ERR_CRASH = "crash";
        public static final String ERR_FREEZE = "Freeze";
        public static final String ERR_ANR = "ANR";
    }

    public static final class ErrorAction {
        public static final String ACTION_CART = "cart";
        public static final String ACTION_ADD_PRODUCT = "Add Product";
        public static final String ACTION_VIEW = "view";
    }

    public static final class PolygonPoint{
        public static final LatLng POINT_1 = new LatLng(Double.parseDouble("50.962713"),Double.parseDouble("2.138803"));
        public static final LatLng POINT_2 = new LatLng(Double.parseDouble("48.807781"),Double.parseDouble("7.888257"));
        public static final LatLng POINT_3 = new LatLng(Double.parseDouble("43.561069"),Double.parseDouble("6.703562"));
        public static final LatLng POINT_4 = new LatLng(Double.parseDouble("43.355179"),Double.parseDouble("-1.708736"));
        public static final LatLng POINT_5 = new LatLng(Double.parseDouble("48.672302"),Double.parseDouble("-4.340165"));
    }

}
