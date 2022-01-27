package com.splunk.rum.demoApp.injection.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.network.RxErrorHandlingCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private final String mBaseUrl;
    private final SplunkRum splunkRum;

    public NetworkModule(String baseUrl, SplunkRum splunkRum) {
        this.mBaseUrl = baseUrl;
        this.splunkRum = splunkRum;
    }

    /**
     * @param application Provide application as parameter
     * @return SharedPreferences instance
     */
    @Provides
    @Singleton
    SharedPreferences provideSharedPreference(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }


    /**
     * @return Gson instance
     */
    @Provides  // Dagger will only look for methods annotated with @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.serializeNulls().create();
    }

    /**
     * Wrap the provided {@link OkHttpClient} with OpenTelemetry and RUM instrumentation. Since
     * {@link Call.Factory} is the primary useful interface implemented by the OkHttpClient, this
     * should be a drop-in replacement for any usages of OkHttpClient.
     * @return A {@link okhttp3.Call.Factory} implementation.
     */
    @Provides
    @Singleton
    Call.Factory provideOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.hostnameVerifier((hostname, session) -> true);

        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(100, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.MINUTES);

        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }

        return splunkRum.createRumOkHttpCallFactory(builder.build());

        //return builder.build();
    }

    /**
     * @param gson Gson instance
     * @return Create an instance using gson for conversion and return GsonConverterFactory
     */
    @Provides
    @Singleton
    GsonConverterFactory providesGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    /**
     * @param gsonConverterFactory GsonConverterFactory use to convert json to model class automatically
     * @param callFactory CallFactory is a call is a request that has been prepared for execution
     * @return Retrofit instance with the configured values.
     */
    @Provides
    @Singleton
    Retrofit provideRetrofit(GsonConverterFactory gsonConverterFactory, Call.Factory callFactory) {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .callFactory(callFactory)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
    }

}
