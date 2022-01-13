package com.splunk.rum.demoApp.injection.module;

import com.splunk.rum.demoApp.injection.scope.UserScope;
import com.splunk.rum.demoApp.model.state.EventServiceInterface;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class EventModule {

    @UserScope
    @Provides
    public EventServiceInterface eventProductService(Retrofit retrofit){
        return retrofit.create(EventServiceInterface.class);
    }

}
