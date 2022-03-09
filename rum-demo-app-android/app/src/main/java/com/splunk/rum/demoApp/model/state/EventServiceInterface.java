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
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface EventServiceInterface {

    @GET
    Observable<BaseResponse> generateHttpNotFound(@Url String url);

    @Multipart
    @POST
    Observable<ResponseBody> generateHttpError(
            @Url String url,
            @Part(AppConstant.FormDataParameter.PRODUCT_ID) RequestBody email,
            @Part(AppConstant.FormDataParameter.QUANTITY) RequestBody address
    );

    @GET
    Observable<ResponseBody> slowApiResponse(@Url String url, @Query("delay") int deplay);
}
