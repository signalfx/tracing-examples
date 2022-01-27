package com.splunk.rum.demoApp.view.base.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.splunk.rum.demoApp.callback.ViewListener;
import com.splunk.rum.demoApp.network.RetrofitException;
import com.splunk.rum.demoApp.util.AppUtils;

public class BaseFragment extends Fragment implements ViewListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showApiError(RetrofitException retrofitException, String errorCode) {
        if (getActivity() != null) {
            AppUtils.handleApiError(getActivity(), retrofitException);
        }
    }
}
