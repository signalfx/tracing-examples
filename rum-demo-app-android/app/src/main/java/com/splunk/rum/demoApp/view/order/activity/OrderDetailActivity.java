package com.splunk.rum.demoApp.view.order.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.databinding.ActivityOrderDetailsBinding;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.home.MainActivity;

import java.util.UUID;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;


public class OrderDetailActivity extends BaseActivity {

    private String uuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize data binding
        ActivityOrderDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        setupToolbar(true);

        //Clear Cart Item
        clearCartItem();

        // Button Click Listener
        binding.btnKeepBrowsing.setOnClickListener(view -> moveActivity(this, MainActivity.class, true, true));

        // RUM Event
        Span checkoutWorkFlow = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_payment));
        checkoutWorkFlow.setStatus(StatusCode.OK, getString(R.string.rum_event_payment_msg));
        checkoutWorkFlow.end();

        String orderId = UUID.randomUUID().toString();
        binding.tvOrderConfirmIdValue.setText(orderId);
        String shippingTrackingId = UUID.randomUUID().toString().substring(0,18);
        binding.tvShippingTrackingIdValue.setText(shippingTrackingId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveActivity(this, MainActivity.class, true, true);
    }

    private void clearCartItem(){
        AppUtils.getProductsFromPref().getProducts().clear();
        RumDemoApp.preferenceRemoveKey(AppConstant.SharedPrefKey.CART_PRODUCTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
