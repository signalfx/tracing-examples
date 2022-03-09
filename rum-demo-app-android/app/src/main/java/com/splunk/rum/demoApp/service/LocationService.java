package com.splunk.rum.demoApp.service;

import static com.splunk.rum.demoApp.util.AppConstant.GLOBAL_ATTR_LONG;
import static com.splunk.rum.demoApp.util.AppConstant.GLOBAL_ATTR_LAT;
import static com.splunk.rum.demoApp.util.AppUtils.getCountryName;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.splunk.rum.demoApp.RumDemoApp;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.PreferenceHelper;
import com.splunk.rum.demoApp.util.StringHelper;

import io.opentelemetry.api.common.AttributeKey;

public class LocationService extends Service {

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
                //noinspection ConstantConditions
                if (location != null) {
                    Double lat = location.getLatitude();
                    Double lon = location.getLongitude();
                    //noinspection ConstantConditions
                    if (lat != null && lon != null) {
                        RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBAL_ATTR_LAT), lat);
                        RumDemoApp.getSplunkRum().setGlobalAttribute(AttributeKey.doubleKey(GLOBAL_ATTR_LONG), lon);
                        String name = getCountryName(LocationService.this, lat, lon);
                        if(StringHelper.isEmpty(name)){
                            PreferenceHelper.setValue(LocationService.this, AppConstant.SharedPrefKey.IS_COUNTRY_NAME_EMPTY,true);
                            PreferenceHelper.setValue(LocationService.this, AppConstant.SharedPrefKey.LAT,String.valueOf(lat));
                            PreferenceHelper.setValue(LocationService.this, AppConstant.SharedPrefKey.LNG,String.valueOf(lon));
                        }else{
                            PreferenceHelper.setValue(LocationService.this, AppConstant.SharedPrefKey.IS_COUNTRY_NAME_EMPTY,false);
                            PreferenceHelper.setValue(LocationService.this, AppConstant.SharedPrefKey.LAT,String.valueOf(lat));
                            PreferenceHelper.setValue(LocationService.this, AppConstant.SharedPrefKey.LNG,String.valueOf(lon));
                        }
                        sendCountryToActivity(name);
                    }
                }
            }
        }, Looper.myLooper());
    }

    private void sendCountryToActivity(String countryName) {
        Intent intent = new Intent(AppConstant.IntentKey.INTENT_KEY);
        intent.putExtra(AppConstant.IntentKey.COUNTRY_NAME, countryName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
