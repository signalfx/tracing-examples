package com.splunk.rum.demoApp.view.base.viewModel;

import android.os.Handler;

import com.splunk.rum.demoApp.callback.BaseViewModelListener;
import com.splunk.rum.demoApp.callback.ViewListener;


/**
 * Base class for all ViewModels.  Reason for this class is that all viewModels need a
 * reference
 * to the UI Thread.
 */
abstract public class BaseViewModel extends BaseViewModelListener<ViewListener> {
    private Handler mUiThreadHandler;

    public void onCreate(Handler handler) {
        mUiThreadHandler = handler;
    }

    public void onDestroy() {
        mUiThreadHandler = null;
    }

    public Handler getUiThreadHandler() {
        return mUiThreadHandler;
    }

    public void setUiThreadHandler(Handler mUiThreadHandler) {
        this.mUiThreadHandler = mUiThreadHandler;
    }
}
