package com.splunk.rum.demoApp.model.state;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface ProductServiceInterface {

    @GET
    Observable<ResponseBody> getProductList(@Url String url);

    @GET
    Observable<ResponseBody> getProductDetail(@Url String url);
}
