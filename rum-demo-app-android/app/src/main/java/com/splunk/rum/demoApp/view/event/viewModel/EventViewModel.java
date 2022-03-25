package com.splunk.rum.demoApp.view.event.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.model.entity.response.BaseResponse;
import com.splunk.rum.demoApp.model.state.EventServiceInterface;
import com.splunk.rum.demoApp.network.RXRetroManager;
import com.splunk.rum.demoApp.network.RetrofitException;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.util.VariantConfig;
import com.splunk.rum.demoApp.view.base.viewModel.BaseViewModel;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class EventViewModel extends BaseViewModel {

    @SuppressWarnings("ALL")
    @Inject
    EventServiceInterface eventServiceInterface;
    @SuppressWarnings("unused")
    private MutableLiveData<BaseResponse> baseResponse;
    private MutableLiveData<ResponseBody> slowAPIResponse;
    private final ResourceProvider resourceProvider;
    private MutableLiveData<Boolean> mIsLoading;

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
    public void generateHttpError(String productId, int quantity) {
        if (view != null && view.isNetworkAvailable()) {
            setIsLoading(true);

            if (StringHelper.isEmpty(productId) && quantity == 0){
                productId = "66VCHSJNUP";
                quantity=1;
            }

            RequestBody productIdBody = RequestBody.create(productId, MediaType.parse("text/plain"));
            RequestBody quantityBody = RequestBody.create(String.valueOf(quantity), MediaType.parse("text/plain"));

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
            }.rxSingleCall(eventServiceInterface.generateHttpError(VariantConfig.getServerBaseUrl() + BuildConfig.HTTP_500_URL, productIdBody, quantityBody));
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
    @SuppressWarnings("ALL")
    public void slowApiResponse() {
        if (view != null && view.isNetworkAvailable()) {
            setIsLoading(true);
            new RXRetroManager<ResponseBody>() {
                @Override
                protected void onSuccess(ResponseBody response) {
                    setIsLoading(false);
                    if (slowAPIResponse != null) {
                        slowAPIResponse.postValue(response);
                    }
                }

                @Override
                protected void onFailure(RetrofitException retrofitException, String errorCode) {
                    super.onFailure(retrofitException, errorCode);
                    setIsLoading(false);
                    //TODO Remove this code after API deployed on server
                    if (slowAPIResponse != null) {
                        slowAPIResponse.postValue(null);
                    }
                    //TODO Uncomment this code after API deployed on server
//                    if (view != null) {
//                        view.showApiError(retrofitException, errorCode);
//                    }

                }
            }.rxSingleCall(eventServiceInterface.slowApiResponse(VariantConfig.getServerBaseUrl() + BuildConfig.SLOW_API_RESPONSE_URL,AppConstant.SLOW_API_SECOND));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(resourceProvider.getString(R.string.slow_api)));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }
    }

    public MutableLiveData<ResponseBody> getSlowAPIResponse() {
        if (slowAPIResponse == null) {
            slowAPIResponse = new MutableLiveData<>();
        }
        return slowAPIResponse;
    }
    @SuppressWarnings("ALL")
    public MutableLiveData<Boolean> getmIsLoading() {
        if (mIsLoading == null) {
            mIsLoading = new MutableLiveData<>();
        }
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        if (mIsLoading != null) {
            mIsLoading.postValue(isLoading);
        }
    }
}
