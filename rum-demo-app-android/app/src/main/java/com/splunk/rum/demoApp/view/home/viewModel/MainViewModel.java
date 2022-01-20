package com.splunk.rum.demoApp.view.home.viewModel;
import androidx.lifecycle.MutableLiveData;

import com.splunk.rum.demoApp.view.base.viewModel.BaseViewModel;


public class MainViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> mIsFromCart = new MutableLiveData<>();

    public MainViewModel() {
        mIsFromCart.setValue(Boolean.FALSE);
    }


    public MutableLiveData<Boolean> getIsFromCart() {
        return mIsFromCart;
    }
}
