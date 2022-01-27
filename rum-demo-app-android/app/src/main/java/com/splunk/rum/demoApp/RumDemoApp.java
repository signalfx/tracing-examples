package com.splunk.rum.demoApp;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.splunk.rum.Config;
import com.splunk.rum.SplunkRum;
import com.splunk.rum.StandardAttributes;
import com.splunk.rum.demoApp.injection.component.DaggerNetworkComponent;
import com.splunk.rum.demoApp.injection.component.DaggerServiceComponent;
import com.splunk.rum.demoApp.injection.component.NetworkComponent;
import com.splunk.rum.demoApp.injection.component.ServiceComponent;
import com.splunk.rum.demoApp.injection.module.AppModule;
import com.splunk.rum.demoApp.injection.module.NetworkModule;
import com.splunk.rum.demoApp.util.VariantConfig;

import io.opentelemetry.api.common.Attributes;

@SuppressWarnings("unused")
public class RumDemoApp extends Application {
    public static NetworkComponent networkComponent;
    public static ServiceComponent serviceComponent;
    private static SharedPreferences.Editor sharedPreferencesEditor;
    private static SharedPreferences sharedPreferences;
    public static SplunkRum splunkRum;

    @Override
    public void onCreate() {
        super.onCreate();
        setupSplunkRUM();
        // Enable Vector Image
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        initDagger();

        sharedPreferencesEditor = networkComponent.provideSharedPreference().edit();
        sharedPreferences = networkComponent.provideSharedPreference();


    }
    /**
     * Init dagger2 network and service component
     */
    public void initDagger(){
        // Dagger 2 Network Component
        networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(VariantConfig.getServerBaseUrl(), splunkRum))
                .appModule(new AppModule(this))
                .build();

        // Dagger 2 Service Component
        serviceComponent = DaggerServiceComponent.builder()
                .networkComponent(networkComponent)
                .build();
    }

    /**
     * Setup Splunk RUM Library
     */
    private void setupSplunkRUM() {
        String appVersion = BuildConfig.VERSION_NAME;
        Config config = SplunkRum.newConfigBuilder()
                //rum.access.token
                .realm(getResources().getString(R.string.rum_realm))
                .rumAccessToken(getResources().getString(R.string.rum_access_token))
                .applicationName(getString(R.string.app_name))
                .deploymentEnvironment(getResources().getString(R.string.rum_environment))
                .debugEnabled(true)
                .globalAttributes(
                        Attributes.builder()
                                .put(StandardAttributes.APP_VERSION, appVersion)
                                .build())
                .build();
        SplunkRum.initialize(config, this);
        splunkRum = SplunkRum.getInstance();
    }

    // Get splunk rum instance
    public static SplunkRum getSplunkRum() {
        if (splunkRum != null) {
            return splunkRum;
        } else {
            return SplunkRum.getInstance();
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        RumDemoApp.sharedPreferences = sharedPreferences;
    }


    // Get Network component
    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }
    // Get Service component
    public static ServiceComponent getServiceComponent() {
        return serviceComponent;
    }

    /**
     * Application level preference work.
     */
    public static void preferencePutInteger(String key, int value) {
        sharedPreferencesEditor.putInt(key, value).apply();
    }

    public static int preferenceGetInteger(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void preferencePutBoolean(String key, boolean value) {
        sharedPreferencesEditor.putBoolean(key, value).apply();
    }

    public static boolean preferenceGetBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void preferencePutString(String key, String value) {
        sharedPreferencesEditor.putString(key, value).apply();
    }

    public static String preferenceGetString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void preferencePutLong(String key, long value) {
        sharedPreferencesEditor.putLong(key, value).apply();
    }

    public static long preferenceGetLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void preferenceRemoveKey(String key) {
        sharedPreferencesEditor.remove(key).apply();
    }

    public void preferencePutFloat(String key, float value) {
        sharedPreferencesEditor.putFloat(key, value).apply();
    }

    public static float preferenceGetFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public static void clearPreference() {
        sharedPreferencesEditor.clear().apply();
    }
}
