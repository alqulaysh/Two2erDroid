package com.se491.app.two2er;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Nithun on 1/27/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    //GoogleApi Client Services:
    GoogleApiClient mGoogleApiClient;

    //Google Map Variable:
    GoogleMap mGoogleMap;

    //Location Variables:
    LocationRequest mLocationReq;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    //Get list of users around me:
    ArrayList<HashMap<String, String>> usersAround;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        usersAround = new ArrayList<>();
        new GetUsers(this).execute();
        System.out.println(usersAround.size());


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            // Error message goes here
        }
        initMap();

    }

    private void initMap() {
        MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFrag.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
//        goToLocationZoom(39.008224,-76.8984527, 15);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

    }

//    private void goToLocationZoom(double lat, double lng, float z) {
//        LatLng ll = new LatLng(lat, lng);
//        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, z);
//        mGoogleMap.moveCamera(update);
//    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationReq = LocationRequest.create();
        mLocationReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationReq.setInterval(2000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReq,this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location){
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //We dont really need to place a marker for the users current position:
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.genuser));

        //markerOptions.position(latLng);
        //markerOptions.title("My Current Position");
        //mCurrLocationMarker = mMap.addMarker(markerOptions);

        //Loop through the user array that we got from the webservice/api of Two2er
        addUsersAroundMe();

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates.36

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void handleFindTutor(View v){
        addUsersAroundMe();
    }
    //Helper Function To Get Users Around Me:
    private void addUsersAroundMe(){
        //new GetUsers(this).execute();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.genuser));

        for(int i = 0; i < usersAround.size(); i++){
            //Get our coords into a String Array by splitting up what was parsed from the JSON:
            String[] coords = usersAround.get(i).get("cords").replace("[", "").replace("]", "").split(",");
            //Split the array into the proper double varaibales:
            double dLong = Double.parseDouble(coords[0]);
            double dLat = Double.parseDouble(coords[1]);

            LatLng lNewLocation = new LatLng(dLat, dLong);

            //Get the users name:
            String sTitle = usersAround.get(i).get("name");

            markerOptions.position(lNewLocation).title(sTitle);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.genuser));

            mGoogleMap.addMarker(markerOptions);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(lNewLocation));
        }
    }
}
