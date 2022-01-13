package com.splunk.rum.demoApp.view.product.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.model.state.ProductServiceInterface;
import com.splunk.rum.demoApp.network.RXRetroManager;
import com.splunk.rum.demoApp.network.RetrofitException;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.viewModel.BaseViewModel;

import javax.inject.Inject;

import okhttp3.ResponseBody;


public class ProductViewModel extends BaseViewModel {

    @Inject
    ProductServiceInterface productServiceInterface;
    private MutableLiveData<ResponseBody> baseResponse;
    private final ObservableBoolean mIsLoading = new ObservableBoolean();
    private final ResourceProvider resourceProvider;

    public ProductViewModel(ResourceProvider resourceProvider) {
        RumDemoApp.getServiceComponent().inject(this);
        this.resourceProvider = resourceProvider;
    }

    /**
     * Initiate the API call and handle the response
     */
    public void getProductList() {
        setIsLoading(true);
        new RXRetroManager<ResponseBody>() {
            @Override
            protected void onSuccess(ResponseBody response) {
                if (view != null) {
                    baseResponse.postValue(response);
                    setIsLoading(false);
                }
            }

            @Override
            protected void onFailure(RetrofitException retrofitException, String errorCode) {
                super.onFailure(retrofitException, errorCode);
                if (view != null) {
                    view.showApiError(retrofitException, errorCode);
                    setIsLoading(false);
                }
            }
        }.rxSingleCall(productServiceInterface.getProductList(BuildConfig.WEB_URL));

    }

    /**
     * Initiate the API call and handle the response
     */
    public void getProductDetail(String productId) {
        setIsLoading(true);
        new RXRetroManager<ResponseBody>() {
            @Override
            protected void onSuccess(ResponseBody response) {
                if (view != null) {
                    baseResponse.postValue(response);
                    setIsLoading(false);
                }
            }

            @Override
            protected void onFailure(RetrofitException retrofitException, String errorCode) {
                super.onFailure(retrofitException, errorCode);
                if (view != null) {
                    view.showApiError(retrofitException, errorCode);
                    setIsLoading(false);
                }
            }
        }.rxSingleCall(productServiceInterface.getProductDetail(BuildConfig.WEB_URL + String.format(resourceProvider.
                getString(R.string.api_product_detail_end_point), productId)));

    }

    public MutableLiveData<ResponseBody> getBaseResponse() {
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
