package com.splunk.rum.demoApp.injection.module;
import com.splunk.rum.demoApp.injection.scope.UserScope;
import com.splunk.rum.demoApp.model.state.ProductServiceInterface;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ProductModule {

    @UserScope
    @Provides
    public ProductServiceInterface provideProductService(Retrofit retrofit){
        return retrofit.create(ProductServiceInterface.class);
    }

}
