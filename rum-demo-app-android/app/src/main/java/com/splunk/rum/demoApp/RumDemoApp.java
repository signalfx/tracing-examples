package com.splunk.rum.demoApp;

import android.app.Application;

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
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.PreferenceHelper;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.util.VariantConfig;

import io.opentelemetry.api.common.Attributes;

@SuppressWarnings("ALL")
public class RumDemoApp extends Application {
    public static NetworkComponent networkComponent;
    public static ServiceComponent serviceComponent;
    public static SplunkRum splunkRum;

    @Override
    public void onCreate() {
        super.onCreate();
        setupSplunkRUM();
        // Enable Vector Image
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        initDagger();

    }

    /**
     * Init dagger2 network and service component
     */
    public void initDagger() {
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

        String token = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.TOKEN, String.class, "");
        String environmentName = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.ENVIRONMENT_NAME, String.class, "");
        String appName = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.APP_NAME, String.class, "");
        if (StringHelper.isEmpty(token) || StringHelper.isEmpty(environmentName)) {
            token = getResources().getString(R.string.rum_access_token);
            environmentName = getResources().getString(R.string.rum_environment);
        }

        String realM = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.REAL_M,
                String.class, "");

        if(StringHelper.isEmpty(realM)){
            realM = getResources().getString(R.string.rum_realm);
        }

        if(StringHelper.isEmpty(appName)){
            appName = getString(R.string.app_name);
        }
        String appVersion = BuildConfig.VERSION_NAME;
        Config config = SplunkRum.newConfigBuilder()
                //rum.access.token
                .realm(realM)
                .rumAccessToken(token)
                .applicationName(appName)
                .deploymentEnvironment(environmentName)
                .debugEnabled(true)
                .globalAttributes(
                        Attributes.builder()
                                .put(StandardAttributes.APP_VERSION, appVersion)
                                .build())
                .build();
        SplunkRum splunkRum = SplunkRum.initialize(config, this);
        RumDemoApp.setSplunkRum(splunkRum);
    }

    // Get splunk rum instance
    public static SplunkRum getSplunkRum() {
        if (splunkRum != null) {
            return splunkRum;
        } else {
            return SplunkRum.getInstance();
        }
    }

    public static void setSplunkRum(SplunkRum splunkRum) {
        RumDemoApp.splunkRum = splunkRum;
    }


    // Get Network component
    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }

    // Get Service component
    public static ServiceComponent getServiceComponent() {
        return serviceComponent;
    }

}
