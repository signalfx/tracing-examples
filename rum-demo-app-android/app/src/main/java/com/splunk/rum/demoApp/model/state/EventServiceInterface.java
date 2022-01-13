package com.splunk.rum.demoApp.model.state;


import com.splunk.rum.demoApp.model.entity.response.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface EventServiceInterface {

    @GET
    Observable<BaseResponse> generateHttpNotFound(@Url String url);

    @GET
    Observable<BaseResponse> generateHttpError(@Url String url);

    @POST
    Observable<BaseResponse> slowApiResponse(@Url String url);
}
