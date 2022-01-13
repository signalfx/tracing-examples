package com.splunk.rum.demoApp.model.state;



import com.splunk.rum.demoApp.model.entity.request.CheckoutRequest;
import com.splunk.rum.demoApp.model.entity.response.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface CheckoutServiceInterface {

    @POST("/cart/checkout")
    Observable<BaseResponse> checkout(@Body CheckoutRequest checkoutRequest);
}
