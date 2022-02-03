package com.splunk.rum.demoApp.model.state;


import com.splunk.rum.demoApp.model.entity.response.BaseResponse;
import com.splunk.rum.demoApp.util.AppConstant;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;


public interface EventServiceInterface {

    @GET
    Observable<BaseResponse> generateHttpNotFound(@Url String url);

    @Multipart
    @POST
    Observable<ResponseBody> generateHttpError(
            @Url String url,
            @Part(AppConstant.FormDataParameter.EMAIL) RequestBody email,
            @Part(AppConstant.FormDataParameter.ADDRESS) RequestBody address,
            @Part(AppConstant.FormDataParameter.ZIPCODE) RequestBody zipCode,
            @Part(AppConstant.FormDataParameter.CITY) RequestBody city,
            @Part(AppConstant.FormDataParameter.STATE) RequestBody state,
            @Part(AppConstant.FormDataParameter.COUNTRY) RequestBody country,
            @Part(AppConstant.FormDataParameter.CC_NUMBER) RequestBody creditCardNumber,
            @Part(AppConstant.FormDataParameter.CC_EX_MONTH) RequestBody creditCardExMonth,
            @Part(AppConstant.FormDataParameter.CC_EX_YEAR) RequestBody creditCardExYear,
            @Part(AppConstant.FormDataParameter.CC_CVV) RequestBody creditCardCvv
    );

    @POST
    Observable<BaseResponse> slowApiResponse(@Url String url);

    @GET
    Observable<BaseResponse> splashApiResponse(@Url String url);
}
