package com.splunk.rum.demoApp.view.event.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewClientCompat;

import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.databinding.FragmentShopWebViewBinding;
import com.splunk.rum.demoApp.util.VariantConfig;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.fragment.BaseFragment;

public class ShopWebViewFragment extends BaseFragment {
    private FragmentShopWebViewBinding binding;

    public ShopWebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShopWebViewBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showProgress();
        }

        binding.webView.setWebViewClient(new webViewClient());
        binding.webView.setWebChromeClient(new chromeClient(this.getContext()));

        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        SplunkRum.getInstance().integrateWithBrowserRum(binding.webView);
        binding.webView.loadUrl(VariantConfig.getServerBaseUrl());
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

    private static class webViewClient extends WebViewClientCompat {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (error.getUrl().startsWith(VariantConfig.getServerBaseUrl())) {
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        }
    }
}