package com.splunk.rum.demoApp.service;

import static com.splunk.rum.demoApp.util.AppConstant.GLOBAL_ATTR_LONG;
import static com.splunk.rum.demoApp.util.AppConstant.GLOBLAL_ATTR_LAT;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.StringHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.opentelemetry.api.common.AttributeKey;

public class LocationService extends Service {
    private final String LOCATION_SERVICE = "LOCATION_SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();
        requestLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void requestLocationUpdates() {
        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = LocationRequest.create()
                .setInterval(AppConstant.LOCATION_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(AppConstant.FASTEST_INTERVAL);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Double lat = location.getLatitude();
                    Double lon = location.getLongitude();

                    if (lat != null && lon != null) {
                        RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBLAL_ATTR_LAT), lat);
                        RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBAL_ATTR_LONG), lon);
                        String name = getCountryName(LocationService.this, lat, lon);

                        if (StringHelper.isNotEmpty(name)) {
                            sendCountryToActivity(name);
                        }
                    }
                }
            }
        }, Looper.myLooper());
    }

    private String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryName();
            }
            return null;
        } catch (IOException ioException) {
            AppUtils.handleRumException(ioException);
        }
        return null;
    }

    private void sendCountryToActivity(String countryName) {
        Intent intent = new Intent(AppConstant.IntentKey.INTENT_KEY);
        intent.putExtra(AppConstant.IntentKey.COUNTRY_NAME, countryName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
