package com.splunk.rum.demoApp.view.urlConfig.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.databinding.ActivityUrlConfigurationBinding;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.util.ValidationUtil;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.home.MainActivity;

import io.opentelemetry.api.common.AttributeKey;


public class URLConfigurationActivity extends BaseActivity {

    private Context mContext;
    private ActivityUrlConfigurationBinding binding;
    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // Initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_url_configuration);
        binding.edtUrl.addTextChangedListener(new TextFieldValidation(binding.edtUrl));
        binding.edtUrl.setText(BuildConfig.BASE_URL);

        binding.btnCallApi.setOnClickListener(view -> {
            if (validateURLAndSetError()) {
                // Navigate to the home screen
                moveActivity(mContext, MainActivity.class, true, true);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private boolean validateURLAndSetError() {
        if (!isValidateURL()) {
            ValidationUtil.setErrorIntoInputTextLayout(binding.edtUrl, binding.urlTextField, getString(R.string.error_url));
            return false;
        } else {
            binding.urlTextField.setError(null);
            return true;
        }
    }

    private void changeButtonState(boolean state) {
        binding.btnCallApi.setEnabled(state);
        binding.btnCallApi.setClickable(state);
    }

    private boolean isValidateURL() {
        if (binding.edtUrl.getText() != null
                && !StringHelper.isEmpty(binding.edtUrl.getText().toString())) {
            return Patterns.WEB_URL.matcher(binding.edtUrl.getText().toString()).matches()
                    && (URLUtil.isHttpUrl(binding.edtUrl.getText().toString())
                    || URLUtil.isHttpsUrl(binding.edtUrl.getText().toString()));
        } else {
            return false;
        }
    }

    /**
     * TextWatcher for all the textInputField
     */
    private class TextFieldValidation implements TextWatcher {
        private final View view;

        /**
         * @param view The view for the get id for compare in afterTextChanged method.
         */
        public TextFieldValidation(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            changeButtonState(!StringHelper.isEmpty(charSequence.toString()) &&
                    !charSequence.toString().equals(getString(R.string.https)));
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.edtUrl && isValidateURL()) {
                //binding.urlTextField.setErrorEnabled(false);
                binding.urlTextField.setError(null);
            }
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastLocation = task.getResult();
                        if (mLastLocation != null) {
                            RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey("Latitude"), mLastLocation.getLatitude());
                            RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey("Longitude"), mLastLocation.getLongitude());
                        }
                    } else {
                        showSnackBar(getString(R.string.no_location_detected));
                    }
                });
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackBar(R.string.permission_rationale, android.R.string.ok,
                    view -> {
                        // Request permission
                        startLocationPermissionRequest();
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The SnackBar text.
     */
    private void showSnackBar(final String text) {
        View container = findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link}.
     *
     * @param mainTextStringId The id for the string resource for the SnackBar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the SnackBar action.
     */
    private void showSnackBar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(URLConfigurationActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                AppConstant.REQUEST_PERMISSIONS_REQUEST_CODE);
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstant.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackBar(R.string.permission_denied_explanation, R.string.settings,
                        view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }
}
