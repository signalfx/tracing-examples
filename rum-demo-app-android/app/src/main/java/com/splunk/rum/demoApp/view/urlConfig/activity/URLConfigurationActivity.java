package com.splunk.rum.demoApp.view.urlConfig.activity;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.splunk.rum.demoApp.util.AppConstant.GLOBAL_ATTR_LAT;
import static com.splunk.rum.demoApp.util.AppConstant.GLOBAL_ATTR_LONG;
import static com.splunk.rum.demoApp.util.AppConstant.REQUEST_CHECK_SETTINGS;
import static com.splunk.rum.demoApp.util.AppUtils.getCountryName;
import static io.opentelemetry.api.common.AttributeKey.stringKey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.databinding.ActivityUrlConfigurationBinding;
import com.splunk.rum.demoApp.service.LocationService;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.PreferenceHelper;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.util.ValidationUtil;
import com.splunk.rum.demoApp.util.VariantConfig;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.event.viewModel.EventViewModel;
import com.splunk.rum.demoApp.view.home.MainActivity;


import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import okhttp3.ResponseBody;


public class URLConfigurationActivity extends BaseActivity {

    private Context mContext;
    private ActivityUrlConfigurationBinding binding;
    private EventViewModel viewModel;
    private final int RUM_EVENT_COUNT = 2;
    private String selectedRealM;
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Location callback to start location request and remove location request
     */
    private LocationCallback mLocationCallback;


    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private LocationRequest mLocationRequest;
    private int apiCount;
    private int spanCount;
    private Span timeToReadyWorkFlow;

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
        if (mFusedLocationClient != null) {
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

        setUpRealmSpinner();
        setValueOfTokenAndEnvironment();

        // Configure ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(EventViewModel.class);
        viewModel.createView(this);

        //get product detail data
        viewModel.getSlowAPIResponse()
                .observe(this,
                        responseBody());


        binding.btnSubmit.setOnClickListener(v -> {
            if (isConfigValueChanged()) {
                changeButtonState(false, binding.btnSubmit);
                changeButtonState(false, binding.btnLogin);
                AppUtils.showError(this, getString(R.string.error_restart_app));
                new Handler().postDelayed(() -> navigateToProductList(v), AppConstant.SPLASH_SCREEN_DURATION);
            } else {
                navigateToProductList(v);
            }
        });


        binding.btnLogin.setOnClickListener(v -> {
            if (validateURLAndSetError()) {
                apiCount = 0;
                spanCount = 0;
                timeToReadyWorkFlow = startWorkflow(getString(R.string.rum_event_time_to_ready));
                callSlowAPI();
            }
        });


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        double screenSize = AppUtils.getScreenSizeInInch(this);

        if (screenSize < AppConstant.SCREEN_SIZE) {
            ViewGroup.LayoutParams layoutParams = binding.btnLogin.getLayoutParams();
            Resources r = getResources();
            layoutParams.height = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
            binding.btnLogin.setLayoutParams(layoutParams);
        }


    }


    /**
     * Set the token and environment name into edit text from local.properties file or preference
     */
    private void setValueOfTokenAndEnvironment() {

        String environmentName = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.ENVIRONMENT_NAME,
                String.class, "");

        String appName = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.APP_NAME,
                String.class, "");

        if (StringHelper.isEmpty(environmentName)) {
            environmentName = getResources().getString(R.string.rum_environment);
        }

        if (StringHelper.isEmpty(appName)) {
            appName = getResources().getString(R.string.app_name);
        }

        // Set the token and environment name into edit text from local.properties file
        binding.edtEnvironment.setText(environmentName);
        binding.edtAppName.setText(appName);
        binding.edtUrl.setText(VariantConfig.getServerBaseUrl());
    }

    private void navigateToProductList(View view) {
        if (validateURLAndSetError()) {
            if (isValidateURL(binding.edtUrl)) {
                int length = String.valueOf(binding.edtUrl.getText()).length();
                @SuppressWarnings("ConstantConditions")
                char lastChar = binding.edtUrl.getText().toString().charAt(length - 1);
                if (String.valueOf(lastChar).equalsIgnoreCase("/")) {
                    VariantConfig.setServerBaseUrl(binding.edtUrl.getText().toString());
                } else {
                    String finalBaseUrl = binding.edtUrl.getText().toString() + "/";
                    VariantConfig.setServerBaseUrl(finalBaseUrl);
                }
                setConfigChangeValue();
            }
            if (view != null && view.getId() == R.id.btnSubmit) {
                Span workflow = startWorkflow(getString(R.string.rum_event_time_to_ready));
                workflow.end();
            }
            // Navigate to the home screen
            PreferenceHelper.removeKey(this, AppConstant.SharedPrefKey.CART_PRODUCTS);
            moveActivity(mContext, MainActivity.class, true, true);
        }
    }


    private boolean isConfigValueChanged() {
        String token = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.TOKEN, String.class, "");
        String environmentName = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.ENVIRONMENT_NAME,
                String.class, getString(R.string.rum_environment));
        String appName = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.APP_NAME, String.class, getString(R.string.app_name));
        String realM = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.REAL_M,
                String.class, getString(R.string.rum_realm));

        if (!selectedRealM.equalsIgnoreCase(realM)) {
            return true;
        } else if (isValidToken() && !token.equalsIgnoreCase(String.valueOf(binding.edtToken.getText()))) {
            return true;
        } else if (isValidAppName() && !appName.equalsIgnoreCase(String.valueOf(binding.edtAppName.getText()))) {
            return true;
        } else if (isValidEnvironmentName()
                && !environmentName.equalsIgnoreCase(String.valueOf(binding.edtEnvironment.getText())))
            return true;

        return false;
    }

    private void setConfigChangeValue() {
        PreferenceHelper.setValue(URLConfigurationActivity.this, AppConstant.SharedPrefKey.REAL_M,
                selectedRealM);

        if (isValidAppName()) {
            PreferenceHelper.setValue(this, AppConstant.SharedPrefKey.APP_NAME,
                    String.valueOf(binding.edtAppName.getText()));
        } else {
            PreferenceHelper.setValue(this, AppConstant.SharedPrefKey.APP_NAME,
                    String.valueOf(getResources().getString(R.string.app_name)));
        }

        if (isValidToken()) {
            PreferenceHelper.setValue(this, AppConstant.SharedPrefKey.TOKEN,
                    String.valueOf(binding.edtToken.getText()));
        } else {
            PreferenceHelper.setValue(this, AppConstant.SharedPrefKey.TOKEN,
                    String.valueOf(getResources().getString(R.string.rum_access_token)));
        }

        if (isValidEnvironmentName()) {
            PreferenceHelper.setValue(this, AppConstant.SharedPrefKey.ENVIRONMENT_NAME,
                    String.valueOf(binding.edtEnvironment.getText()));
        } else {
            PreferenceHelper.setValue(this, AppConstant.SharedPrefKey.ENVIRONMENT_NAME,
                    String.valueOf(getResources().getString(R.string.rum_environment)));
        }
    }


    private void callSlowAPI() {
        showProgress();
        viewModel.slowApiResponse();
    }

    /**
     * @return Handle API Response
     */
    private androidx.lifecycle.Observer<ResponseBody> responseBody() {
        return response -> {
            apiCount++;
            if (apiCount < RUM_EVENT_COUNT) {
                viewModel.slowApiResponse();
            } else {
                parentOne();
            }
        };
    }

    private void parentOne() {
        spanCount++;
        Span parentSpan = SplunkRum.getInstance().getOpenTelemetry()
                .getTracer(getString(R.string.rum_instrumentation_name)).spanBuilder(getString(R.string.rum_event_parent))
                .setParent(io.opentelemetry.context.Context.current())
                .setAttribute(stringKey(getString(R.string.rum_screen_name)), URLConfigurationActivity.class.getSimpleName())
                .startSpan();
        new Handler().postDelayed(() -> {
            childOne(parentSpan);
            parentSpan.end();
        }, AppConstant.RUM_CUSTOM_EVENT_2_SEC_DELAY);
    }

    private void childOne(Span parentSpan) {
        Span childSpan = SplunkRum.getInstance().getOpenTelemetry()
                .getTracer(getString(R.string.rum_instrumentation_name)).spanBuilder(getString(R.string.rum_event_child))
                .setParent(io.opentelemetry.context.Context.current().with(parentSpan))
                .setAttribute(stringKey(getString(R.string.rum_screen_name)), URLConfigurationActivity.class.getSimpleName())
                .startSpan();

        new Handler().postDelayed(() -> {
            childSpan.end();
            if (spanCount < RUM_EVENT_COUNT) {
                parentOne();
            } else {
                timeToReadyWorkFlow.end();
                hideProgress();
                if (isConfigValueChanged()) {
                    changeButtonState(false, binding.btnSubmit);
                    changeButtonState(false, binding.btnLogin);
                    AppUtils.showError(this, getString(R.string.error_restart_app));
                    new Handler().postDelayed(() -> navigateToProductList(null), AppConstant.SPLASH_SCREEN_DURATION);
                } else {
                    navigateToProductList(null);
                }
            }
        }, AppConstant.RUM_CUSTOM_EVENT_4_SEC_DELAY);
    }

    public Span startWorkflow(String workflowName) {
        return SplunkRum.getInstance().getOpenTelemetry()
                .getTracer(getString(R.string.rum_instrumentation_name))
                .spanBuilder(workflowName)
                // will only work if the parent was created on the same thread
                .setParent(io.opentelemetry.context.Context.current())
                .setAttribute(stringKey(getString(R.string.rum_work_flow_name)), workflowName)
                .setAttribute(stringKey(getString(R.string.rum_screen_name)), URLConfigurationActivity.class.getSimpleName())
                .startSpan();
    }

    private void startLocationService() {
        startService(new Intent(this, LocationService.class));
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        startLocationService();

        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest.create()
                .setInterval(AppConstant.LOCATION_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(AppConstant.FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();


        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> result = settingsClient.checkLocationSettings(locationSettingsRequest);

        result.addOnCompleteListener(task -> {
            try {
                //noinspection unused
                LocationSettingsResponse response = task.getResult(ApiException.class);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
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


    /**
     * onLocationChanged method call when location change
     *
     * @param location Location class as parameter
     */
    public void onLocationChanged(Location location) {
        if (location != null) {
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            //noinspection ConstantConditions
            if (lat != null && lon != null) {
                RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBAL_ATTR_LAT), lat);
                RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBAL_ATTR_LONG), lon);
                String name = getCountryName(mContext, lat, lon);
                if (StringHelper.isEmpty(name)) {
                    PreferenceHelper.setValue(mContext, AppConstant.SharedPrefKey.IS_COUNTRY_NAME_EMPTY, true);
                    PreferenceHelper.setValue(mContext, AppConstant.SharedPrefKey.LAT, String.valueOf(lat));
                    PreferenceHelper.setValue(mContext, AppConstant.SharedPrefKey.LNG, String.valueOf(lon));
                } else {
                    PreferenceHelper.setValue(mContext, AppConstant.SharedPrefKey.IS_COUNTRY_NAME_EMPTY, false);
                    PreferenceHelper.setValue(mContext, AppConstant.SharedPrefKey.LAT, String.valueOf(lat));
                    PreferenceHelper.setValue(mContext, AppConstant.SharedPrefKey.LNG, String.valueOf(lon));
                }
                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        @SuppressWarnings("unused") final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // All required changes were successfully made
                requestCurrentLocation();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showSnackBar(getString(R.string.no_location_detected));
                // The user was asked to change settings, but chose not to
            } else {
                showSnackBar(getString(R.string.no_location_detected));
            }
        }
    }


    private boolean validateURLAndSetError() {
        if (!isValidateURL(binding.edtUrl)) {
            ValidationUtil.setErrorIntoInputTextLayout(binding.edtUrl, binding.urlTextField, getString(R.string.error_url));
            return false;
        } else {
            binding.urlTextField.setError(null);
            return true;
        }
    }

    private void changeButtonState(boolean state, MaterialButton button) {
        button.setEnabled(state);
        button.setClickable(state);
    }

    private boolean isValidateURL(TextInputEditText textInputEditText) {
        if (textInputEditText.getText() != null
                && !StringHelper.isEmpty(textInputEditText.getText().toString())) {
            return Patterns.WEB_URL.matcher(textInputEditText.getText().toString()).matches()
                    && (URLUtil.isHttpUrl(textInputEditText.getText().toString())
                    || URLUtil.isHttpsUrl(textInputEditText.getText().toString()));
        } else {
            return false;
        }
    }

    private boolean isValidAppName() {
        return binding.edtAppName.getText() != null
                && StringHelper.isNotEmpty(binding.edtAppName.getText().toString());
    }


    private boolean isValidToken() {
        return binding.edtToken.getText() != null
                && StringHelper.isNotEmpty(binding.edtToken.getText().toString());
    }

    private boolean isValidEnvironmentName() {
        return binding.edtEnvironment.getText() != null
                && StringHelper.isNotEmpty(binding.edtEnvironment.getText().toString());
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
            if (view.getId() == R.id.edtUrl && isValidateURL(binding.edtUrl)) {
                changeButtonState(!StringHelper.isEmpty(charSequence.toString()) &&
                        !charSequence.toString().equalsIgnoreCase(getString(R.string.https)), binding.btnSubmit);
                changeButtonState(!StringHelper.isEmpty(charSequence.toString()) &&
                        !charSequence.toString().equalsIgnoreCase(getString(R.string.https)), binding.btnLogin);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.edtUrl && isValidateURL(binding.edtUrl)) {
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
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
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

    private void setUpRealmSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.realm, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        binding.realMSpinner.setAdapter(adapter);
        binding.spinnerArrow.setOnClickListener(view -> binding.realMSpinner.performClick());
        binding.txtRealm.setOnClickListener(view -> binding.realMSpinner.performClick());
        selectedRealM = adapter.getItem(0).toString();
        String realM = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.REAL_M,
                String.class, "");

        if (StringHelper.isEmpty(realM)) {
            realM = getString(R.string.rum_realm);
        }
        int position = adapter.getPosition(realM);
        binding.realMSpinner.setSelection(position);

        binding.realMSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Object selectedItem = adapterView.getItemAtPosition(position);
                if (selectedItem != null && !StringHelper.isEmpty(selectedItem.toString())) {
                    selectedRealM = selectedItem.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cancellationTokenSource != null) {
            cancellationTokenSource.cancel();
        }
    }
}
