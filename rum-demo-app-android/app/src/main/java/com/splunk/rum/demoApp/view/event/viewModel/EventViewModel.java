package com.splunk.rum.demoApp.view.event.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.model.entity.request.CheckoutRequest;
import com.splunk.rum.demoApp.model.entity.response.BaseResponse;
import com.splunk.rum.demoApp.model.state.EventServiceInterface;
import com.splunk.rum.demoApp.network.RXRetroManager;
import com.splunk.rum.demoApp.network.RetrofitException;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.MonthUtil;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.util.VariantConfig;
import com.splunk.rum.demoApp.view.base.viewModel.BaseViewModel;

import java.io.IOException;
import java.util.Calendar;

import javax.inject.Inject;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class EventViewModel extends BaseViewModel {

    @Inject
    EventServiceInterface eventServiceInterface;
    private MutableLiveData<BaseResponse> baseResponse;
    private final ResourceProvider resourceProvider;
    private final ObservableBoolean mIsLoading = new ObservableBoolean();

    public EventViewModel(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
        RumDemoApp.getServiceComponent().inject(this);
    }


    /**
     * Initiate the API call and handle the response
     */
    public void generateHttpNotFound() {
        if (view != null && view.isNetworkAvailable()) {
            setIsLoading(true);
            new RXRetroManager<BaseResponse>() {
                @Override
                protected void onSuccess(BaseResponse response) {
                    setIsLoading(false);
                    baseResponse.postValue(response);
                }

                @Override
                protected void onFailure(RetrofitException retrofitException, String errorCode) {
                    super.onFailure(retrofitException, errorCode);
                    setIsLoading(false);
                    if (view != null) {
                        view.showApiError(retrofitException, errorCode);
                    }

                }
            }.rxSingleCall(eventServiceInterface.generateHttpNotFound(VariantConfig.getServerBaseUrl() + BuildConfig.HTTP_404_URL));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(resourceProvider.getString(R.string.method_not_found)));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }
    }

    /**
     * Initiate the API call and handle the response
     */
    public void generateHttpError() {
        if (view != null && view.isNetworkAvailable()) {
            setIsLoading(true);

            int initMonth = Calendar.getInstance().get(Calendar.MONTH);
            int year = Calendar.getInstance().get(Calendar.YEAR); // If 2022 pass into API then its return 504
            String currentMonthSortName = MonthUtil.getShortMonthNameList().get(initMonth);
            CheckoutRequest checkoutRequest = new CheckoutRequest(
                    "someone@example.com", "1600 Amphitheatre Parkway", "94043", "Mountain View",
                    "CA", "United States",
                    "4432801561520454", 1, String.valueOf(year), "672", currentMonthSortName
            );
            RequestBody emailBody = RequestBody.create(checkoutRequest.getEmail(), MediaType.parse("text/plain"));
            RequestBody addressBody = RequestBody.create(checkoutRequest.getAddress(), MediaType.parse("text/plain"));
            RequestBody zipBody = RequestBody.create(checkoutRequest.getZipCode(), MediaType.parse("text/plain"));
            RequestBody cityBody = RequestBody.create(checkoutRequest.getCity(), MediaType.parse("text/plain"));
            RequestBody stateBody = RequestBody.create(checkoutRequest.getState(), MediaType.parse("text/plain"));
            RequestBody countryBody = RequestBody.create(checkoutRequest.getCountry(), MediaType.parse("text/plain"));
            RequestBody ccNumberBody = RequestBody.create(checkoutRequest.getCreditCardNumber(), MediaType.parse("text/plain"));
            RequestBody ccExMonth = RequestBody.create(String.valueOf(checkoutRequest.getMonth()), MediaType.parse("text/plain"));
            RequestBody ccExYear = RequestBody.create(checkoutRequest.getYear(), MediaType.parse("text/plain"));
            RequestBody ccCvv = RequestBody.create(checkoutRequest.getCvv(), MediaType.parse("text/plain"));

            new RXRetroManager<ResponseBody>() {
                @Override
                protected void onSuccess(ResponseBody response) {
                    setIsLoading(false);
                }

                @Override
                protected void onFailure(RetrofitException retrofitException, String errorCode) {
                    super.onFailure(retrofitException, errorCode);
                    setIsLoading(false);
                    if (view != null) {
                        view.showApiError(retrofitException, errorCode);
                    }
                }
            }.rxSingleCall(eventServiceInterface.generateHttpError(VariantConfig.getServerBaseUrl() + BuildConfig.API_CHECK_OUT_END_POINT, emailBody, addressBody,
                    zipBody, cityBody, stateBody, countryBody,
                    ccNumberBody, ccExMonth, ccExYear, ccCvv));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(resourceProvider.getString(R.string.http_error)));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }
    }

    /**
     * Initiate the API call and handle the response
     */
    public void slowApiResponse() {
        if (view != null && view.isNetworkAvailable()) {
            setIsLoading(true);
            new RXRetroManager<BaseResponse>() {
                @Override
                protected void onSuccess(BaseResponse response) {
                    setIsLoading(false);
                    if(baseResponse != null){
                        baseResponse.postValue(response);
                    }
                }

                @Override
                protected void onFailure(RetrofitException retrofitException, String errorCode) {
                    super.onFailure(retrofitException, errorCode);
                    setIsLoading(false);
                    if (view != null) {
                        view.showApiError(retrofitException, errorCode);
                    }

                }
            }.rxSingleCall(eventServiceInterface.slowApiResponse(BuildConfig.SLOW_API_RESPONSE_URL));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(resourceProvider.getString(R.string.slow_api)));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }
    }

    /**
     * Initiate the API call and handle the response
     */
    public void splashDummyApiCall() {

        setIsLoading(true);
        Span workflow = SplunkRum.getInstance().startWorkflow(resourceProvider.getString(R.string.rum_event_app_start_dummy_api_work_flow));
        new RXRetroManager<BaseResponse>() {
            @Override
            protected void onSuccess(BaseResponse response) {
                setIsLoading(false);
                workflow.setStatus(StatusCode.OK, resourceProvider.getString(R.string.rum_event_app_start_dummy_api_success));
                workflow.end();
            }

            @Override
            protected void onFailure(RetrofitException retrofitException, String errorCode) {
                super.onFailure(retrofitException, errorCode);
                setIsLoading(false);
                workflow.setStatus(StatusCode.ERROR, resourceProvider.getString(R.string.rum_event_app_start_dummy_api_fail));
                workflow.end();
                if (view != null) {
                    view.showApiError(retrofitException, errorCode);
                }

            }
        }.rxSingleCall(eventServiceInterface.slowApiResponse(BuildConfig.SPLASH_DUMMY_API_URL));
    }

    public MutableLiveData<BaseResponse> getBaseResponse() {
        if (baseResponse == null) {
            baseResponse = new MutableLiveData<>();
        }
        return baseResponse;
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }
}
