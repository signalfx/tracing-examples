package com.splunk.rum.demoApp.util;

public class VariantConfig {

    private static String serverBaseUrl = "http://pmrum.o11ystore.com/";

    public static void setServerBaseUrl(String baseUrl) {
        serverBaseUrl = baseUrl;
    }

    public static String getServerBaseUrl() {
        return serverBaseUrl;
    }
}
