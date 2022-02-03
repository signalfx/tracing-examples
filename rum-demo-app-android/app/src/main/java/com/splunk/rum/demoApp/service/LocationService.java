package com.splunk.rum.demoApp.service;

import static com.splunk.rum.demoApp.util.AppConstant.GLOBAL_ATTR_LONG;
import static com.splunk.rum.demoApp.util.AppConstant.GLOBLAL_ATTR_LAT;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.util.AppConstant;

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
                    RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBLAL_ATTR_LAT), location.getLatitude());
                    RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBAL_ATTR_LONG), location.getLongitude());
                }
            }
        }, Looper.myLooper());
    }
}
