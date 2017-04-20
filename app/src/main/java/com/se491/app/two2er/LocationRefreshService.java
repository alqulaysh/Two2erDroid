package com.se491.app.two2er;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.stormpath.sdk.Stormpath;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by pazra on 4/15/2017.
 */

public class LocationRefreshService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String serviceLogTag = "LocationRefreshService";
    private static final String objectName = "Two2er Location Refresh Service"; // Makes this object easy to find when debugging
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 10f;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    public LocationRefreshService() {
        super(objectName);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(serviceLogTag, "Connected to GoogleApiClient " + bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(serviceLogTag, "Connection suspended to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(serviceLogTag, "Connected to GoogleApiClient");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(serviceLogTag, "lat " + location.getLatitude());
        Log.i(serviceLogTag, "lng " + location.getLongitude());
        LatLng mLocation = (new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onCreate() {
        mGoogleApiClient = MyGoogleApiClient_Singleton.getInstance(null).get_GoogleApiClient();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(serviceLogTag, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(serviceLogTag, "Starting Location Refresh Service");

        mGoogleApiClient = MyGoogleApiClient_Singleton.getInstance(null).get_GoogleApiClient();

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( LocationRefreshService.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( LocationRefreshService.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return super.onStartCommand(intent, flags, startId);
        }

        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (loc != null) {
            Log.i(serviceLogTag, "lat " + loc.getLatitude() + " : long " + loc.getLongitude());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            startLocationUpdate();
        }
        catch (Exception ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void initLocationRequest() {
        Log.i(serviceLogTag, "initLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void startLocationUpdate() {
        Log.i(serviceLogTag, "startLocationUpdate");
        initLocationRequest();

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( LocationRefreshService.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( LocationRefreshService.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void SendLocationData(String latitude, String longitude) {
        RequestBody requestBody = new FormBody.Builder()
                .add("location", "")
                .build();

        Request request = new Request.Builder()
                .url(ServerApiUtilities.GetServerApiUrl() + "studentlocations/")
                .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                .post(requestBody)
                .build();
    }
}
