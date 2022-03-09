package com.splunk.rum.demoApp.callback;


import com.splunk.rum.demoApp.network.RetrofitException;
@SuppressWarnings("ALL")
public interface ViewListener {


    void showProgress();

    void hideProgress();

    void showApiError(RetrofitException retrofitException, String errorCode);

    boolean isNetworkAvailable();
}
