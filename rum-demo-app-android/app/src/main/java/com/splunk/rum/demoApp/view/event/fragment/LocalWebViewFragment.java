package com.splunk.rum.demoApp.view.event.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.databinding.FragmentLocalWebViewBinding;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.PreferenceHelper;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.fragment.BaseFragment;

import io.opentelemetry.api.common.Attributes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalWebViewFragment} factory method to
 * create an instance of this fragment.
 */
public class LocalWebViewFragment extends BaseFragment {
    private WebViewAssetLoader webViewAssetLoader;
    private FragmentLocalWebViewBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (this.getContext() != null) {
            if (this.getContext() instanceof BaseActivity) {
                ((BaseActivity) this.getContext()).showProgress();
            }
            this.webViewAssetLoader = new WebViewAssetLoader.Builder()
                    .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this.getContext()))
                    .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this.getContext()))
                    .build();
        }

        // Inflate the layout for this fragment
        binding = FragmentLocalWebViewBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.webViewAssetLoader != null && getContext() != null) {
            binding.webView.setWebViewClient(new LocalContentWebViewClient(this.webViewAssetLoader));
            binding.webView.setWebChromeClient(new chromeClient(this.getContext()));
            binding.webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");

            binding.webView.getSettings().setJavaScriptEnabled(true);
            binding.webView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
            SplunkRum.getInstance().integrateWithBrowserRum(binding.webView);
        }
    }

    private static class chromeClient extends WebChromeClient {
        private final Context context;

        public chromeClient(Context context) {
            this.context = context;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).hideProgress();
                }
            }
        }
    }

    private static class LocalContentWebViewClient extends WebViewClientCompat {
        private final WebViewAssetLoader assetLoader;

        private LocalContentWebViewClient(WebViewAssetLoader assetLoader) {
            this.assetLoader = assetLoader;
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return assetLoader.shouldInterceptRequest(request.getUrl());
        }
    }

    public static class WebAppInterface {
        private final Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            SplunkRum.getInstance().addRumEvent("WebViewButtonClicked", Attributes.empty());
            Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public String getRumAccessToken() {
            String token = PreferenceHelper.getValue(context, AppConstant.SharedPrefKey.TOKEN, String.class, "");
            if (StringHelper.isEmpty(token) ) {
                token = context.getResources().getString(R.string.rum_access_token);
            }
            return token;
        }

        @JavascriptInterface
        public String getRumRealm() {
            String realM = PreferenceHelper.getValue(context, AppConstant.SharedPrefKey.REAL_M,
                    String.class, "");

            if(StringHelper.isEmpty(realM)){
                realM = context.getResources().getString(R.string.rum_realm);
            }
            return realM;
        }
    }
}