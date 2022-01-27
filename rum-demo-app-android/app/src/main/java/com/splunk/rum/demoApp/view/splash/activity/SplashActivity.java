package com.splunk.rum.demoApp.view.splash.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.databinding.ActivitySplashBinding;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.event.viewModel.EventViewModel;
import com.splunk.rum.demoApp.view.urlConfig.activity.URLConfigurationActivity;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private Context mContext;
    private Span appStartWorkFlow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        appStartWorkFlow = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_logged_in_work_flow));

        // Initialize data binding
        ActivitySplashBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        // Configure ViewModel
        EventViewModel eventViewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(EventViewModel.class);
        binding.setViewModel(eventViewModel);
        eventViewModel.createView(this);
        binding.setLifecycleOwner(this);

        //eventViewModel.splashDummyApiCall();

        new Handler().postDelayed(() -> {
            //End the custom event app start
            if (appStartWorkFlow != null) {
                appStartWorkFlow.setStatus(StatusCode.OK, getString(R.string.rum_event_logged_in_msg));
                appStartWorkFlow.end();
            }
            moveActivity(mContext, URLConfigurationActivity.class, true, true);
        }, AppConstant.SPLASH_SCREEN_DURATION);
    }
}
