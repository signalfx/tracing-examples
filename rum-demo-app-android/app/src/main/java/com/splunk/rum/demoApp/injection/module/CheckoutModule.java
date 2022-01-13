package com.splunk.rum.demoApp.injection.module;
import com.splunk.rum.demoApp.injection.scope.UserScope;
import com.splunk.rum.demoApp.model.state.CheckoutServiceInterface;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class CheckoutModule {

    @UserScope
    @Provides
    public CheckoutServiceInterface provideCheckoutService(Retrofit retrofit){
        return retrofit.create(CheckoutServiceInterface.class);
    }

}
