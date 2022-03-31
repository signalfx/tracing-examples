package com.splunk.rum.demoApp.view.splash.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.urlConfig.activity.URLConfigurationActivity;


public class AppStartingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize data binding
        DataBindingUtil.setContentView(this, R.layout.activity_splash);
        new Handler().postDelayed(() -> moveActivity(this, URLConfigurationActivity.class, true, true), AppConstant.SPLASH_SCREEN_DURATION);
    }
}
