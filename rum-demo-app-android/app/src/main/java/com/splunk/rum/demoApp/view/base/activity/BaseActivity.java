package com.splunk.rum.demoApp.view.base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.callback.DialogButtonClickListener;
import com.splunk.rum.demoApp.callback.ViewListener;
import com.splunk.rum.demoApp.network.RetrofitException;
import com.splunk.rum.demoApp.util.AlertDialogHelper;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.util.progressDialog.ProgressDialogHelper;
import com.splunk.rum.demoApp.view.checkout.activity.CheckOutActivity;
import com.splunk.rum.demoApp.view.urlConfig.activity.URLConfigurationActivity;


public class BaseActivity extends AppCompatActivity implements ViewListener, DialogButtonClickListener {

    private ProgressDialogHelper progressDialogHelper;
    private boolean isSalesTax;


    /**
     * @param isShowBack Using this boolean param the custom navigation back button can show/hide in toolbar.
     */
    public void setupToolbar(boolean isShowBack) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            if (isShowBack) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                toolbar.setNavigationContentDescription(getString(R.string.header_back_btn));
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }
    }

    /**
     * If you want to show toolbar without back button
     * then please call this method
     */
    public void setupToolbar() {
        setupToolbar(false);
    }


    /**
     * @param context          activity or fragment context
     * @param destinationClass Destination navigation class
     * @param finish           Boolean to finish the current activity
     * @param clearStack       Clear the activity stack
     * @param bundle           Bundle data
     */
    public void moveActivity(Context context, Class<?> destinationClass, boolean finish, boolean clearStack, Bundle bundle) {
        Intent intent = new Intent(context, destinationClass);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        if (clearStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
        Activity activity = (Activity) context;
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        if (finish) {
            ((Activity) context).finish();
        }
    }

    /**
     * @param context          activity or fragment context
     * @param destinationClass Destination navigation class
     * @param finish           Boolean to finish the current activity
     */
    @SuppressWarnings("unused")
    public void moveActivity(Context context, Class<?> destinationClass, boolean finish) {
        moveActivity(context, destinationClass, finish, false, null);
    }

    /**
     * @param context          activity or fragment context
     * @param destinationClass Destination navigation class
     * @param finish           Boolean to finish the current activity
     * @param clearStack       Clear the activity stack
     */
    public void moveActivity(Context context, Class<?> destinationClass, boolean finish, boolean clearStack) {
        moveActivity(context, destinationClass, finish, clearStack, null);
    }


    /**
     * @param context activity or fragment context
     * @param message string message
     */
    public void showProgressDialog(Context context, String message) {
        if (progressDialogHelper == null) {
            progressDialogHelper = new ProgressDialogHelper(context);
        }
        if (StringHelper.isEmpty(message)) {
            progressDialogHelper.showCircularProgressDialog();
        } else {
            progressDialogHelper.showProgressDialog(message);
        }

    }

    /**
     * hide current visible progress dialog
     */
    public void hideProgressDialog() {
        if (progressDialogHelper != null) {
            progressDialogHelper.hideProgressDialog();
            progressDialogHelper.hideCircularProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && menu.getItem(i).getItemId() == R.id.configChange) {
                menu.getItem(i).setContentDescription(getString(R.string.header_context_menu_item));
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.configChange) {
            moveActivity(this, URLConfigurationActivity.class, true, true);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showProgress() {
        showProgressDialog(this, "");
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
    }

    @SuppressWarnings("ALL")
    @Override
    public void showApiError(RetrofitException retrofitException, String errorCode) {
        hideProgress();

        if (errorCode.equalsIgnoreCase(AppConstant.ERROR_INTERNET)
                && retrofitException != null
                && retrofitException.getMessage() != null) {
            isSalesTax = retrofitException.getMessage().equalsIgnoreCase(getString(R.string.new_sales_tax));
            AlertDialogHelper.showDialog(this, null, this.getString(R.string.error_network)
                    , this.getString(R.string.ok), this.getString(R.string.retry), false,
                    this, AppConstant.DialogIdentifier.INTERNET_DIALOG);
        } else {
            AppUtils.handleApiError(this, retrofitException);
        }
        if (this instanceof CheckOutActivity) {
            AppUtils.enableDisableBtn(true,
                    ((CheckOutActivity) this).getBinding().btnPlaceOrder);
        } else if (this instanceof URLConfigurationActivity) {
            AppUtils.enableDisableBtn(true,
                    ((URLConfigurationActivity) this).getBinding().btnLogin);
        }

    }

    @SuppressWarnings("unused")
    @Override
    public boolean isNetworkAvailable() {
        return AppUtils.isNetworkAvailable(this);
    }

    /**
     * @param activity Visible activity
     */
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onPositiveButtonClicked(int dialogIdentifier) {

    }

    @Override
    public void onNegativeButtonClicked(int dialogIdentifier) {
        if (dialogIdentifier == AppConstant.DialogIdentifier.INTERNET_DIALOG) {
            if (this instanceof CheckOutActivity) {
                if (isSalesTax) {
                    ((CheckOutActivity) this).getCheckoutViewModel().generateNewSalesTax();
                } else {
                    ((CheckOutActivity) this).getCheckoutViewModel().doCheckOut();
                }
            } else if (this instanceof URLConfigurationActivity) {
                ((URLConfigurationActivity) this).getBinding().btnLogin.performClick();
            }
        }
    }
}