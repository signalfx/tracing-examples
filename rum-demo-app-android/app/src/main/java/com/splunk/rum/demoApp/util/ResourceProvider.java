package com.splunk.rum.demoApp.util;

import android.content.res.Resources;

import androidx.annotation.StringRes;

public class ResourceProvider {
    private final Resources mResources;
    public ResourceProvider(Resources resource) {
        this.mResources = resource;
    }

    public String getString(@StringRes Integer stringResId){
        return mResources.getString(stringResId);
    }
}
