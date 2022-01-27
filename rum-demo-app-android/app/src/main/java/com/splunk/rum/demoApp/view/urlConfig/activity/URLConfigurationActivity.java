package com.splunk.rum.demoApp.view.urlConfig.activity;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.splunk.rum.demoApp.util.AppConstant.REQUEST_CHECK_SETTINGS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.databinding.ActivityUrlConfigurationBinding;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.util.ValidationUtil;
import com.splunk.rum.demoApp.util.VariantConfig;
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
    private Location mLastLocation;

    /**
     * Location callback to start location request and remove location request
     */
    private LocationCallback mLocationCallback;



    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private LocationRequest mLocationRequest;

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                startLocationUpdates();
            }
        } else {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mFusedLocationClient = getFusedLocationProviderClient(this);

        // Initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_url_configuration);
        binding.edtUrl.addTextChangedListener(new TextFieldValidation(binding.edtUrl));
        binding.edtUrl.setText(VariantConfig.getServerBaseUrl());



        binding.btnCallApi.setOnClickListener(view -> {
            if (validateURLAndSetError()) {

                int length = binding.edtUrl.getText().toString().length();
                char lastChar = binding.edtUrl.getText().toString().charAt(length - 1);
                if (String.valueOf(lastChar).equalsIgnoreCase("/")) {
                    VariantConfig.setServerBaseUrl(binding.edtUrl.getText().toString());
                } else {
                    String finalBaseUrl = binding.edtUrl.getText().toString() + "/";
                    VariantConfig.setServerBaseUrl(finalBaseUrl);
                }
                // Navigate to the home screen
                moveActivity(mContext, MainActivity.class, true, true);
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 10000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 5 sec */
        long FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();


        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> result = settingsClient.checkLocationSettings(locationSettingsRequest);

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!checkPermissions()) {
                        requestPermissions();
                    }else{
                        requestCurrentLocation();
                    }
                }
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    URLConfigurationActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        showSnackBar(getString(R.string.no_location_detected));
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });


    }


    /** onLocationChanged method call when location change
     * @param location Location class as parameter
     */
    public void onLocationChanged(Location location) {
        if(location != null){
            showSnackBar(getString(R.string.location_detected));
            RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey("_sf_geo_lat"), location.getLatitude());
            RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey("_sf_geo_long"), location.getLongitude());
            if(mFusedLocationClient != null){
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // All required changes were successfully made
                requestCurrentLocation();
            }else if(resultCode == Activity.RESULT_CANCELED){
                showSnackBar(getString(R.string.no_location_detected));
                // The user was asked to change settings, but chose not to
            }else{
                showSnackBar(getString(R.string.no_location_detected));
            }
        }
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
     * Gets current location.
     * Note: The code checks for permission before calling this method, that is, it's never called
     * from a method with a missing permission. Also, I include a second check with my extension
     * function in case devs just copy/paste this code.
     */
    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {
        if (checkPermissions()) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,
                    Looper.myLooper());
        } else {
            requestPermissions();
        }
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int permissionState = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            return permissionState == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(URLConfigurationActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                startLocationUpdates();
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

    @Override
    protected void onStop() {
        super.onStop();
        if (cancellationTokenSource != null) {
            cancellationTokenSource.cancel();
        }
    }
}
