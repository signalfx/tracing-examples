package com.splunk.rum.demoApp.util;

import android.content.res.Resources;

import androidx.annotation.StringRes;

public class ResourceProvider {
    private final Resources mResources;
    public ResourceProvider(Resources resource) {
        this.mResources = resource;
    }

    /**
     * @param stringResId The id for the string resource
     * @return string from the string.xml file using stringResId
     */
    public String getString(@StringRes Integer stringResId){
        return mResources.getString(stringResId);
    }
}
