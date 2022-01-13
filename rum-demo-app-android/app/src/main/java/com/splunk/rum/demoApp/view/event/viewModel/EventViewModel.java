package com.splunk.rum.demoApp.view.event.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.model.entity.response.BaseResponse;
import com.splunk.rum.demoApp.model.state.EventServiceInterface;
import com.splunk.rum.demoApp.network.RXRetroManager;
import com.splunk.rum.demoApp.network.RetrofitException;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.viewModel.BaseViewModel;

import javax.inject.Inject;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;


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
        setIsLoading(true);
        // TODO need to confirm if custom event needs to be sent for the below use case
        Span workflow = SplunkRum.getInstance().startWorkflow(resourceProvider.getString(R.string.rum_event_start_404));
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
                workflow.setStatus(StatusCode.ERROR, resourceProvider.getString(R.string.rum_event_api_return_404));
                workflow.end();
                if (view != null) {
                    view.showApiError(retrofitException, errorCode);
                }

            }
        }.rxSingleCall(eventServiceInterface.generateHttpNotFound(BuildConfig.MOCK_404_URL));
    }

    /**
     * Initiate the API call and handle the response
     */
    public void generateHttpError() {
        setIsLoading(true);
        // TODO need to confirm if custom event needs to be sent for the below use case
        Span workflow = SplunkRum.getInstance().startWorkflow(resourceProvider.getString(R.string.rum_event_start_500));
        new RXRetroManager<BaseResponse>() {
            @Override
            protected void onSuccess(BaseResponse response) {
                setIsLoading(false);
            }

            @Override
            protected void onFailure(RetrofitException retrofitException, String errorCode) {
                super.onFailure(retrofitException, errorCode);
                setIsLoading(false);
                workflow.setStatus(StatusCode.ERROR, resourceProvider.getString(R.string.rum_event_api_return_500));
                workflow.end();
                if(view != null){
                    view.showApiError(retrofitException, errorCode);
                }


            }
        }.rxSingleCall(eventServiceInterface.generateHttpError(BuildConfig.MOCK_500_URL));
    }

    /**
     * Initiate the API call and handle the response
     */
    public void slowApiResponse() {
        setIsLoading(true);
        // TODO need to confirm if custom event needs to be sent for the below use case
        Span workflow = SplunkRum.getInstance().startWorkflow(resourceProvider.getString(R.string.rum_event_start_slow_response));
        new RXRetroManager<BaseResponse>() {
            @Override
            protected void onSuccess(BaseResponse response) {
                setIsLoading(false);
                workflow.setStatus(StatusCode.OK, resourceProvider.getString(R.string.rum_event_api_return_slow_response));
                workflow.end();
            }

            @Override
            protected void onFailure(RetrofitException retrofitException, String errorCode) {
                super.onFailure(retrofitException, errorCode);
                setIsLoading(false);
                workflow.setStatus(StatusCode.ERROR, resourceProvider.getString(R.string.rum_event_api_fail_slow_slow_response));
                workflow.end();
                if(view != null){
                    view.showApiError(retrofitException, errorCode);
                }

            }
        }.rxSingleCall(eventServiceInterface.slowApiResponse(BuildConfig.SLOW_API_RESPONSE_URL));
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
