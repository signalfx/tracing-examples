package com.splunk.rum.demoApp.model.state;


import com.splunk.rum.demoApp.util.AppConstant;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;


public interface ProductServiceInterface {

    @GET
    Observable<ResponseBody> getProductList(@Url String url);

    @GET
    Observable<ResponseBody> getProductDetail(@Url String url);

    @GET()
    Observable<ResponseBody> getCartItems(@Url String url);

    @Multipart
    @POST()
    Observable<ResponseBody> addToCart(@Url String url, @Part(AppConstant.FormDataParameter.QUANTITY) RequestBody quantity,
                                       @Part(AppConstant.FormDataParameter.PRODUCT_ID) RequestBody productId);
}
