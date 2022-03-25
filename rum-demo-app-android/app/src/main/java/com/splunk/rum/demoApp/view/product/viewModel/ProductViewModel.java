package com.splunk.rum.demoApp.view.product.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.model.state.ProductServiceInterface;
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


public class ProductViewModel extends BaseViewModel {

    @Inject
    ProductServiceInterface productServiceInterface;
    private MutableLiveData<ResponseBody> baseResponse;
    private MutableLiveData<ResponseBody> addProductToCartResponse;
    private MutableLiveData<Boolean> mIsLoading;
    private final ResourceProvider resourceProvider;

    public ProductViewModel(ResourceProvider resourceProvider) {
        RumDemoApp.getServiceComponent().inject(this);
        this.resourceProvider = resourceProvider;
    }

    /**
     * Initiate the API call and handle the response
     */
    public void getProductList() {
        if (view != null && view.isNetworkAvailable()) {
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
            }.rxSingleCall(productServiceInterface.getProductList(VariantConfig.getServerBaseUrl()));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(""));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }
    }

    /**
     * Initiate the API call and handle the response
     */
    public void getProductDetail(String productId) {
        if (view != null && view.isNetworkAvailable()) {
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
            }.rxSingleCall(productServiceInterface.getProductDetail(VariantConfig.getServerBaseUrl() + String.format(resourceProvider.
                    getString(R.string.api_product_detail_end_point), productId)));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(""));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }

    }

    /**
     * Initiate the API call and handle the response
     */
    public void getCartItems() {
        if (view != null && view.isNetworkAvailable()) {
            setIsLoading(true);
            new RXRetroManager<ResponseBody>() {
                @Override
                protected void onSuccess(ResponseBody response) {
                    baseResponse.postValue(response);
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
            }.rxSingleCall(productServiceInterface.getCartItems(VariantConfig.getServerBaseUrl() + BuildConfig.API_CART_END_POINT));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(""));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }

    }

    /**
     * Initiate the API call and handle the response
     */
    public void addToCart(String quantity, String productId) {
        if (view != null && view.isNetworkAvailable()) {
            if (StringHelper.isNotEmpty(quantity) && StringHelper.isNotEmpty(productId)) {
                RequestBody quantityBody = RequestBody.create(quantity, MediaType.parse("text/plain"));
                RequestBody productIdBody = RequestBody.create(productId, MediaType.parse("text/plain"));

                setIsLoading(true);
                new RXRetroManager<ResponseBody>() {
                    @Override
                    protected void onSuccess(ResponseBody response) {
                        addProductToCartResponse.postValue(response);
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
                }.rxSingleCall(productServiceInterface.addToCart(VariantConfig.getServerBaseUrl() + BuildConfig.API_CART_END_POINT, quantityBody, productIdBody));
            }
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(resourceProvider.getString(R.string.rum_event_add_to_cart)));
                view.showApiError(retrofitException, AppConstant.ERROR_INTERNET);
            }
        }
    }


    public MutableLiveData<ResponseBody> getBaseResponse() {
        if (baseResponse == null) {
            baseResponse = new MutableLiveData<>();
        }
        return baseResponse;
    }

    @SuppressWarnings("ALL")
    public MutableLiveData<Boolean> getmIsLoading() {
        if (mIsLoading == null) {
            mIsLoading = new MutableLiveData<>();
        }
        return mIsLoading;
    }

    public MutableLiveData<ResponseBody> getAddProductToCartResponse() {
        if (addProductToCartResponse == null) {
            addProductToCartResponse = new MutableLiveData<>();
        }
        return addProductToCartResponse;
    }

    public void setIsLoading(boolean isLoading) {
        if (mIsLoading != null) {
            mIsLoading.postValue(isLoading);
        }
    }
}
