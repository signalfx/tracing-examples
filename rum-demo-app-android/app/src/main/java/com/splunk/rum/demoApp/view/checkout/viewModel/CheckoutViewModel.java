package com.splunk.rum.demoApp.view.checkout.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.model.entity.request.CheckoutRequest;
import com.splunk.rum.demoApp.model.entity.response.BaseResponse;
import com.splunk.rum.demoApp.model.state.CheckoutServiceInterface;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class CheckoutViewModel extends BaseViewModel {

    private CheckoutRequest checkoutRequest;
    @Inject
    CheckoutServiceInterface checkoutServiceInterface;
    private MutableLiveData<ResponseBody> baseResponse;
    private MutableLiveData<BaseResponse> salesTaxResponse;
    private final ObservableBoolean mIsLoading = new ObservableBoolean();
    private final ResourceProvider resourceProvider;

    public CheckoutViewModel(ResourceProvider resourceProvider) {
        RumDemoApp.getServiceComponent().inject(this);
        this.resourceProvider = resourceProvider;
        int initMonth = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR) + 1; // If 2022 pass into API then its return 504
        String currentMonthSortName = MonthUtil.getShortMonthNameList().get(initMonth);
        this.checkoutRequest = new CheckoutRequest(
                "someone@example.com", "1600 Amphitheatre Parkway", "94043", "Mountain View", "CA", "United States",
                "4432801561520454", 1, String.valueOf(year), "672", currentMonthSortName
        );
    }

    public CheckoutRequest getCheckoutRequest() {
        return checkoutRequest;
    }

    public void setCheckoutRequest(CheckoutRequest checkoutRequest) {
        this.checkoutRequest = checkoutRequest;
    }

    /**
     * Initiate the API call and handle the response
     */
    public void doCheckOut() {
        if (view != null && view.isNetworkAvailable()) {
            view.showProgress();
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
                    if (view != null) {
                        baseResponse.postValue(response);
                        view.hideProgress();
                    }
                }

                @Override
                protected void onFailure(RetrofitException retrofitException, String errorCode) {
                    super.onFailure(retrofitException, errorCode);
                    if (view != null) {
                        view.showApiError(retrofitException, errorCode);
                        view.hideProgress();
                    }
                }
            }.rxSingleCall(checkoutServiceInterface.checkOut(VariantConfig.getServerBaseUrl() + BuildConfig.API_CHECK_OUT_END_POINT, emailBody, addressBody, zipBody, cityBody, stateBody, countryBody, ccNumberBody, ccExMonth, ccExYear, ccCvv));
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
    public void generateNewSalesTax() {
        if (view != null && view.isNetworkAvailable()) {
            setIsLoading(true);
            new RXRetroManager<BaseResponse>() {
                @Override
                protected void onSuccess(BaseResponse response) {
                    setIsLoading(false);
                    salesTaxResponse.postValue(response);
                }

                @Override
                protected void onFailure(RetrofitException retrofitException, String errorCode) {
                    super.onFailure(retrofitException, errorCode);
                    setIsLoading(false);
                }
            }.rxSingleCall(checkoutServiceInterface.generateNewSalesTax(
                    VariantConfig.getServerBaseUrl() + BuildConfig.HTTP_GENERATE_NEW_SALES_TAX));
        } else {
            if (view != null) {
                RetrofitException retrofitException = RetrofitException.networkError(new IOException(resourceProvider.getString(R.string.new_sales_tax)));
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

    public MutableLiveData<BaseResponse> getSalesTaxResponse() {
        if (salesTaxResponse == null) {
            salesTaxResponse = new MutableLiveData<>();
        }
        return salesTaxResponse;
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }
}
