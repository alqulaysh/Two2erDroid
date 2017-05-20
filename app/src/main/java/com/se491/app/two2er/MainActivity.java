package com.se491.app.two2er;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Window;
import android.view.WindowManager;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.se491.app.two2er.Activities.StartPage.LoginActivity;
import com.se491.app.two2er.Activities.StartPage.RegisterActivity;
import com.se491.app.two2er.HelperObjects.CurrentUser;
import com.se491.app.two2er.HelperObjects.MyGoogleApiClient_Singleton;
import com.se491.app.two2er.Services.LocationRefreshService;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.StormpathLogger;
import com.stormpath.sdk.models.StormpathError;




public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{



    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    static final Integer GPS_SETTINGS = 0x7;
    static final Integer READ_EXST = 0x4;
    private final String TAG = "MainActivity";



    private GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions.length > 0) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e(MainActivity.class.getSimpleName(), "Connected to Google Play Services!");
        MyGoogleApiClient_Singleton.getInstance(mGoogleApiClient);

        askForPermission(new String []{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION | READ_EXST);

        while(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

       while(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

        }

        if (!Stormpath.isInitialized()) {
            Stormpath.setLogLevel(StormpathLogger.VERBOSE);
            StormpathConfiguration stormpathConfiguration = new StormpathConfiguration.Builder()
                    .baseUrl("https://two2er.apps.stormpath.io/")
                    .build();
            Stormpath.init(this, stormpathConfiguration);
        }

        if (Stormpath.getAccessToken() != null) {
            Stormpath.refreshAccessToken(new StormpathCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(MainActivity.this, SideMenuActivity.class));
                    finish();
                }

                @Override
                public void onFailure(StormpathError error) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        }

        if (FirebaseInstanceId.getInstance() != null) {
            Log.d("Refreshed Token", "FB Token: " + FirebaseInstanceId.getInstance().getToken());
        }

        SetupForApplication();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(MainActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
    }

    private void SetupForApplication() {
        Log.i(TAG, "Running SetupForApplication");
        CurrentUser.Init();

        Intent intent = new Intent(this, LocationRefreshService.class);
        if (intent != null) {
            this.startService(intent);
        }
    }


    private void askForPermission(String[] permissions, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[0]) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[1])) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, permissions, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, permissions, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


}
